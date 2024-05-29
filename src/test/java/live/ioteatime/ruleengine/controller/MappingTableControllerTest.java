package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.service.MappingTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MappingTableController.class)
class MappingTableControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    MappingTableService mappingTableService;

    @Test
    void getMappingTableTest() throws Exception {
        doNothing().when(mappingTableService).getMappingTable();

        mockMvc.perform(get("/update/mapping-table")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("update mapping-table")));

        verify(mappingTableService).getMappingTable();
    }

}