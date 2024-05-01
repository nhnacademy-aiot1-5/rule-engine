package live.ioteatime.ruleengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.ioteatime.ruleengine.domain.InfluxQuery;
import live.ioteatime.ruleengine.domain.QueryRequest;
import live.ioteatime.ruleengine.manager.QueryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(InfluxQueryController.class)
class InfluxQueryControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    QueryManager queryManager;
    @MockBean
    InfluxQuery influxQuery;

    @Test
    void setQuery() throws Exception {
        QueryRequest queryRequest = new QueryRequest();

        when(queryManager.setUp(any(QueryRequest.class))).thenReturn("SELECT * FROM measurement");
        doNothing().when(influxQuery).setQuery(anyString());

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(queryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create Query")));

        verify(influxQuery).setQuery("SELECT * FROM measurement");
    }

}