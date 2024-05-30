package live.ioteatime.ruleengine.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.ioteatime.ruleengine.adaptor.ClientAdaptor;
import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.domain.TopicDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class WebClientServiceImplTest {
    @Mock
    ClientAdaptor clientAdaptor;
    @InjectMocks
    private WebClientServiceImpl webClientService;
    TopicDto topicDto;
    MqttModbusDTO mqttModbusDTO;

    ObjectMapper objectMapper = new ObjectMapper();
    String json = "{\"protocol\":\"exampleProtocol\",\"id\":\"exampleId\",\"time\":1716718698871,\"value\":12.34}";
    String url = "testUrl";

    @BeforeEach
    void setUp() throws JsonProcessingException {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(webClientService, "controlUrlFormat", url);
        topicDto = new TopicDto("place", "type", "phase", "des");
        mqttModbusDTO = objectMapper.readValue(json, MqttModbusDTO.class);

    }

    @Test
    void sendOutlierToFront() throws JsonProcessingException {
        doNothing().when(clientAdaptor).sendPostRequest(anyString(), any());

        webClientService.sendOutlierToFront("test", topicDto, mqttModbusDTO, "sensor");

        verify(clientAdaptor).sendPostRequestFront(anyString(), any());

    }

    @Test
    void sendOutlierToApi() {
        doNothing().when(clientAdaptor).sendPostRequest(anyString(), any());

        webClientService.sendOutlierToApi("test", topicDto, mqttModbusDTO);

        verify(clientAdaptor).sendPostRequest(anyString(), any());
    }

    @Test
    void lightControl() {
        doNothing().when(clientAdaptor).sendGetRequest(anyString());

        webClientService.lightControl("test", "on");

        verify(clientAdaptor).sendGetRequest(anyString());
    }

}