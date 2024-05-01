package live.ioteatime.ruleengine.manager;

import live.ioteatime.ruleengine.domain.InfluxQuery;
import live.ioteatime.ruleengine.domain.QueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class QueryManager {
    private final InfluxQuery influxQuery;
    StringBuilder query = new StringBuilder();

    public String setUp(QueryRequest queryRequest) {
        cleanBuilder();
        setLocalTime();
        setBucket(queryRequest.getBucket());
        setRange(queryRequest.getRange());
        settingFilter(queryRequest);
        setWindow(queryRequest.getWindow(),queryRequest.getFn(),queryRequest.getYield());
        influxQuery.getQueries().add(query.toString());

        log.info("save influx query : {}",query);

        return query.toString();
    }

    private void cleanBuilder() {
        query.setLength(0);
    }

    private void setLocalTime() {
        query.append("import \"timezone\"").append("option location = timezone.fixed(offset: 9h) ");
    }

    private void settingFilter(QueryRequest queryRequest) {
        Map<String,String> filters = queryRequest.getFilters();
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (Map.Entry<String,String> entry : filters.entrySet()) {
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

    private void setWindow(String window, String fn,String output) {
        query.append("|> aggregateWindow(every: ").append(window).append(", fn: ").append(fn).append(", createEmpty: false) ")
                .append("|> yield(name: \"").append(output).append("\")");
    }

}
