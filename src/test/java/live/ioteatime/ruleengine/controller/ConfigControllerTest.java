package live.ioteatime.ruleengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.ioteatime.ruleengine.domain.MqttInfo;
import live.ioteatime.ruleengine.service.CreateProperties;
import live.ioteatime.ruleengine.service.JschService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ConfigController.class)
class ConfigControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    CreateProperties createProperties;
    @MockBean
    JschService jschService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void addBroker() throws Exception {
        MqttInfo mqttInfo = new MqttInfo();
        List<String> topics = List.of("data/#", "data/#", "data/#");

        ReflectionTestUtils.setField(mqttInfo, "mqttHost", "localhost");
        ReflectionTestUtils.setField(mqttInfo, "mqttId", "test");
        ReflectionTestUtils.setField(mqttInfo, "mqttTopic", topics);
        String filePath = "/src/asdadsa.properties";

        when(createProperties.createConfig(any(MqttInfo.class))).thenReturn(filePath);
        doNothing().when(jschService).scpFile(anyString(), anyString());

        mockMvc.perform(post("/brokers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mqttInfo)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create Properties ")));

        verify(jschService).scpFile(eq(filePath), eq(mqttInfo.getMqttId()));
        verify(createProperties).createConfig(any(MqttInfo.class));
    }

    @Test
    void deleteBroker() throws Exception {
        doNothing().when(jschService).deleteBridge(anyString());

        mockMvc.perform(get("/delete/{bridgeName}", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Delete Bridge ")));

        verify(jschService).deleteBridge(anyString());
    }

}
