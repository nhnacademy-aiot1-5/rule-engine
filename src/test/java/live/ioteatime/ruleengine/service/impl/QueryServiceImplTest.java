package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.QueryRequest;
import live.ioteatime.ruleengine.repository.ChannelsRepository;
import live.ioteatime.ruleengine.repository.InfluxQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryServiceImplTest {
    @Mock
    InfluxQueryRepository influxQueryRepository;
    @Mock
    BufferedReader bufferedReader;
    @Mock
    FileReader fileReader;
    @Mock
    ChannelsRepository channelsRepository;
    @InjectMocks
    QueryServiceImpl service;

    @Value("${my.query.path}")
    String queryPath;

    @BeforeEach
    void setUp() throws Exception {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addQuery() {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setBucket("test");
        queryRequest.setRange("test");
        queryRequest.setFilters(Map.of("test","Test","test2","Test2"));
        queryRequest.setWindow("test");
        queryRequest.setFn("test");
        queryRequest.setYield("test");

        service.addQuery(queryRequest);

        verify(influxQueryRepository).writeQuery(anyString());
        verify(influxQueryRepository).updateQuery(queryPath);

    }

    @Test
    void getQueries() throws IOException {
    }

    @Test
    void deleteQuery() {
    }

    @Test
    void getChannelId() {
    }
}