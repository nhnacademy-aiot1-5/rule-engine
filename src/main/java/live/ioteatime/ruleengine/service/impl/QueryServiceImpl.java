package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.QueryRequest;
import live.ioteatime.ruleengine.domain.QueryResponse;
import live.ioteatime.ruleengine.domain.Tags;
import live.ioteatime.ruleengine.repository.ChannelsRepository;
import live.ioteatime.ruleengine.repository.InfluxQueryRepository;
import live.ioteatime.ruleengine.service.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {
    private final InfluxQueryRepository influxQueryRepository;
    private final ChannelsRepository channelsRepository;
    StringBuilder query = new StringBuilder();
    Pattern pattern = Pattern.compile("\\[\"(\\w+)\"] == \"(\\w+)\"");
    @Value("${my.query.path}")
    String queryPath;

    @PostConstruct
    private void initQuery() {
        influxQueryRepository.updateQuery(queryPath);
    }

    @Override
    public void addQuery(QueryRequest queryRequest) {
        cleanBuilder();
        setLocalTime();
        setBucket(queryRequest.getBucket());
        setRange(queryRequest.getRange());
        settingFilter(queryRequest);
        setWindow(queryRequest.getWindow(), queryRequest.getFn(), queryRequest.getYield());
        influxQueryRepository.writeQuery(query.toString());
        influxQueryRepository.updateQuery(queryPath);

        log.info("save influx query : {}", query);
    }

    @Override
    public List<QueryResponse> getQueries() {
        List<QueryResponse> queries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(queryPath))) {
            String line;
            int index = 0;

            while ((line = reader.readLine()) != null) {
                QueryResponse queryResponse = QueryResponse.builder().number(index).query(line).build();
                queries.add(queryResponse);
                index += 1;
            }
            log.info("queries {}", queries);
        } catch (IOException e) {
            log.error("query file is not found : {}", e.getMessage());
        }

        return queries;
    }

    @Override
    public String deleteQuery(int index) {
        String remove = influxQueryRepository.removeQuery(index);
        influxQueryRepository.modifyQuery();
        influxQueryRepository.updateQuery(queryPath);
        log.info("delete query : {}", remove);

        return remove;
    }

    @Override
    public int getChannelId(int index) {
        Tags tags = splitQuery(index);

        return channelsRepository.findChannelIdByTags(tags.getType(), tags.getPhase(), tags.getDescription(), tags.getPlace());
    }

    private Tags splitQuery(int index) {
        String s = influxQueryRepository.getQuery(index);
        Matcher matcher = pattern.matcher(s);
        Tags tags = new Tags();

        while (matcher.find()) {
            String field = matcher.group(1);
            String value = matcher.group(2);

            switch (field) {
                case "place":
                    tags.setPlace(value);
                    break;
                case "type":
                    tags.setType(value);
                    break;
                case "phase":
                    tags.setPhase(value);
                    break;
                case "description":
                    tags.setDescription(value);
                    break;
                default:
                    break;
            }

        }
        log.info("split query {}", tags);

        return tags;
    }

    private void cleanBuilder() {
        query.setLength(0);
    }

    private void setLocalTime() {
        query.append("import \"timezone\"").append("option location = timezone.fixed(offset: 9h) ");
    }

    private void settingFilter(QueryRequest queryRequest) {
        Map<String, String> filters = queryRequest.getFilters();
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }
        for (int i = 0; i < keys.size(); i++) {
            setFilter(keys.get(i), values.get(i));
        }
    }

    private void setBucket(String bucket) {
        query.append("from(bucket: \"").append(bucket).append("\") ");
    }

    private void setRange(String range) {
        query.append("|> range(start: ").append(range).append(") ");
    }

    private void setFilter(String filterName, String filter) {
        query.append("|> filter(fn: (r) => r[\"").append(filterName).append("\"] == \"").append(filter).append("\") ");
    }

    private void setWindow(String window, String fn, String output) {
        query.append("|> aggregateWindow(every: ").append(window).append(", fn: ").append(fn).append(", createEmpty: false) ")
                .append("|> yield(name: \"").append(output).append("\")");
    }

}
