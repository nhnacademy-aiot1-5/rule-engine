package live.ioteatime.ruleengine.repository;

import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Component
@RequiredArgsConstructor
public class InfluxQueryRepository {
    private List<String> queries = new ArrayList<>();
    private final QueryApi queryApi;

    public List<FluxTable> query(String query) {
        return queryApi.query(query);
    }

    public int getQuerySize() {
        return queries.size();
    }

    public String getQuery(int index) {
        return queries.get(index);
    }
}
