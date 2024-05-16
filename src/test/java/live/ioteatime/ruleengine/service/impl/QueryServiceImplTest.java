package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.QueryRequest;
import live.ioteatime.ruleengine.domain.QueryResponse;
import live.ioteatime.ruleengine.repository.ChannelsRepository;
import live.ioteatime.ruleengine.repository.impl.InfluxQueryRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QueryServiceImplTest {
    @Mock
    InfluxQueryRepositoryImpl influxQueryRepository;
    @Mock
    ChannelsRepository channelsRepository;
    @InjectMocks
    QueryServiceImpl queryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String queryPath = "path/to/query/file";
        ReflectionTestUtils.setField(influxQueryRepository, "queryPath", queryPath);

        queryService = new QueryServiceImpl(influxQueryRepository, channelsRepository) {
            @Override
            protected BufferedReader createBufferedReader(String queryPath) {
                return new BufferedReader(new StringReader("query1\nquery2\nquery3"));
            }
        };

    }

    @Value("${my.query.path}")
    String queryPath;

    @Test
    void addQuery() {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setBucket("test");
        queryRequest.setRange("test");
        queryRequest.setFilters(Map.of("test", "Test", "test2", "Test2"));
        queryRequest.setWindow("test");
        queryRequest.setFn("test");
        queryRequest.setYield("test");

        queryService.addQuery(queryRequest);

        verify(influxQueryRepository).writeQuery(anyString());
        verify(influxQueryRepository).updateQuery(queryPath);
    }

    @Test
    void getQueries() {
        List<QueryResponse> queries = queryService.getQueries();

        assertEquals(3, queries.size());
        assertEquals("query1", queries.get(0).getQuery());
        assertEquals("query2", queries.get(1).getQuery());
        assertEquals("query3", queries.get(2).getQuery());

    }

    @Test
    void deleteQuery() {
        int index = 0;
        String path = "asd";
        when(influxQueryRepository.removeQuery(index)).thenReturn("test");
        when(influxQueryRepository.getQueryPath()).thenReturn(path);

        queryService.deleteQuery(index);

        verify(influxQueryRepository).removeQuery(index);
        verify(influxQueryRepository).modifyQuery();
        verify(influxQueryRepository).updateQuery(anyString());
    }

    @Test
    void getChannelId() {
        String query1 = "import \"timezone\"option location = timezone.fixed(offset: 9h) from(bucket: \"test\") |> range(start: -2d) |> filter(fn: (r) => r[\"place\"] == \"test\") |> filter(fn: (r) => r[\"type\"] == \"test\") |> filter(fn: (r) => r[\"phase\"] == \"test\") |> filter(fn: (r) => r[\"description\"] == \"test\") |> aggregateWindow(every: 1h, fn: last, createEmpty: false) |> yield(name: \"last\")\n";

        when(influxQueryRepository.getQuery(1)).thenReturn(query1);
        when(channelsRepository.findChannelIdByTags(anyString(), anyString(), anyString(), anyString())).thenReturn(1);

        int channelId = queryService.getChannelId(1);

        assertEquals(1, channelId);
        verify(channelsRepository).findChannelIdByTags(anyString(), anyString(), anyString(), anyString());
    }
}