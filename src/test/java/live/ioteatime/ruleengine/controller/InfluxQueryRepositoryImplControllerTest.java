package live.ioteatime.ruleengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.ioteatime.ruleengine.repository.InfluxQueryRepository;
import live.ioteatime.ruleengine.domain.QueryRequest;
import live.ioteatime.ruleengine.domain.QueryResponse;
import live.ioteatime.ruleengine.service.impl.QueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(InfluxQueryController.class)
class InfluxQueryRepositoryImplControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    QueryServiceImpl queryServiceImpl;
    @MockBean
    InfluxQueryRepository influxQueryRepository;

    @Test
    void updateQueryTest() throws Exception {
        QueryRequest queryRequest = new QueryRequest();

        doNothing().when(queryServiceImpl).addQuery(any());

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(queryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create Query")));

        verify(queryServiceImpl).addQuery(any());
    }

    @Test
    void getQueryTest() throws Exception {
        QueryResponse queryResponse = QueryResponse.builder().number(0).query("test").build();
        List<QueryResponse> queryResponseList = List.of(queryResponse);

        when(queryServiceImpl.getQueries()).thenReturn(queryResponseList);

        mockMvc.perform(get("/query")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].number").value(0))
                .andExpect(jsonPath("$[0].query").value("test"));
    }

    @Test
    void deleteQueryTest() throws Exception {
        String remove = "test";

        when(queryServiceImpl.deleteQuery(anyInt())).thenReturn(remove);

        mockMvc.perform(get("/delete/query/{index}", 0)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("delete " + remove));

        verify(queryServiceImpl).deleteQuery(anyInt());
    }

}
