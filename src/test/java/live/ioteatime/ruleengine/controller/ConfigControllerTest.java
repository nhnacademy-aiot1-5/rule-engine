package live.ioteatime.ruleengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.ioteatime.ruleengine.domain.ModbusInfo;
import live.ioteatime.ruleengine.domain.MqttInfo;
import live.ioteatime.ruleengine.service.CreateProperties;
import live.ioteatime.ruleengine.service.JschService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfigController.class)
class ConfigControllerTest {
    @MockBean
    CreateProperties createProperties;
    @MockBean
    JschService jschService;
    @Autowired
    MockMvc mockMvc;

    @Test
    void mqttBridge() throws Exception {
        Constructor<MqttInfo> constructor = MqttInfo.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        MqttInfo mqttInfo = constructor.newInstance();
        List<String> topics = List.of("data/#", "data/#", "data/#");

        ReflectionTestUtils.setField(mqttInfo, "mqttHost", "localhost");
        ReflectionTestUtils.setField(mqttInfo, "mqttId", "test");
        ReflectionTestUtils.setField(mqttInfo, "mqttTopic", topics);
        String filePath = "/src/asdadsa.properties";

        when(createProperties.createConfig(any(MqttInfo.class))).thenReturn(filePath);
        doNothing().when(jschService).scpFile(anyString(), anyString(), anyString());

        mockMvc.perform(post("/mqtt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mqttInfo)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("create mqtt Bridge properties ")));

        verify(jschService).scpFile(eq(filePath), eq(mqttInfo.getMqttId()), anyString());
        verify(createProperties).createConfig(any(MqttInfo.class));
    }

    @Test
    void deleteBroker() throws Exception {
        doNothing().when(jschService).deleteBridge(anyString(), anyString());

        mockMvc.perform(get("/delete/{type}/{bridgeName}", "test", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Delete Bridge ")));

        verify(jschService).deleteBridge(anyString(), anyString());
    }

    @Test
    void modbusBridgeTest() throws Exception {
        Constructor<ModbusInfo> constructor = ModbusInfo.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ModbusInfo modbusInfo = constructor.newInstance();
        ReflectionTestUtils.setField(modbusInfo, "name", "localhost");
        ReflectionTestUtils.setField(modbusInfo, "host", "localhost");
        ReflectionTestUtils.setField(modbusInfo, "channel", "800/2,12/21");
        String filePath = "/src/asdadsa.properties";

        when(createProperties.createConfig(any(ModbusInfo.class))).thenReturn(filePath);
        doNothing().when(jschService).scpFile(anyString(), anyString(), anyString());

        mockMvc.perform(post("/modbus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(modbusInfo)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("create modbus Bridge properties ")));

        verify(jschService).scpFile(eq(filePath), eq(modbusInfo.getName()), anyString());
        verify(createProperties).createConfig(any(ModbusInfo.class));
    }

}
