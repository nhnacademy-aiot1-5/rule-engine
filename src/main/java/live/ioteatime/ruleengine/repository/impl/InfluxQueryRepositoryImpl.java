package live.ioteatime.ruleengine.repository.impl;

import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import live.ioteatime.ruleengine.repository.InfluxQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InfluxQueryRepositoryImpl implements InfluxQueryRepository {
    private final List<String> queries = new ArrayList<>();
    private final QueryApi queryApi;
    @Value("${my.query.path}")
    private String queryPath;

    public List<FluxTable> query(String query) {
        return queryApi.query(query);
    }

    public int getQuerySize() {
        return queries.size();
    }

    public String getQuery(int index) {
        return queries.get(index);
    }

    public void updateQuery(String queryPath) {
        queries.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(queryPath))) {
            String str;

            while ((str = reader.readLine()) != null) {
                queries.add(str);
            }
            log.info("query read {}", queries.size());
        } catch (FileNotFoundException e) {
            log.error("file not found {}", e.getMessage());
        } catch (IOException e) {
            log.error("read query file : {}", e.getMessage(), e);
        }
    }

    public void writeQuery(String query) {
        File file = new File(queryPath);

        try {
            if (!file.exists() && file.createNewFile()) {
                log.info("create query file : {}", file.getAbsolutePath());
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
                bufferedWriter.write(query + "\n");
            }
        } catch (IOException e) {
            log.error("write query file : {}", file.getAbsolutePath(), e);
        }
    }

    public void modifyQuery() {
        try {
            Files.delete(Path.of(queryPath));
            File file = new File(queryPath);

            if (!file.exists() && file.createNewFile()) {
                log.info("delete and create query file : {}", file.getAbsolutePath());
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
                for (String s : queries) {
                    bufferedWriter.write(s + "\n");
                }
            }
        } catch (IOException e) {
            log.error("read query file : {}", e.getMessage());
        }
    }

    public String removeQuery(int index) {
        return queries.remove(index);
    }

    @Override
    public String getQueryPath() {
        return queryPath;
    }

}
