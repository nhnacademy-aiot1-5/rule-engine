package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.adaptor.ClientAdaptor;
import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.domain.SaveOutlierDto;
import live.ioteatime.ruleengine.domain.SendOutlierDto;
import live.ioteatime.ruleengine.domain.TopicDto;
import live.ioteatime.ruleengine.service.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientServiceImpl implements WebClientService {
    private final ClientAdaptor clientAdaptor;

    @Value("${control.dev.eui}")
    private String devEui;
    @Value("${control.url.format}")
    private String controlUrlFormat;
    @Value("${control.base.uri}")
    private String controlBaseUri;
    @Value("${api.base.uri}")
    private String apiBaseUri;
    @Value("${front.base.uri}")
    private String frontBaseUri;

    @Override
    public void sendOutlierToFront(String endPoint, TopicDto topicDto, MqttModbusDTO mqttModbusDTO, String sensorName) {
        String url = frontBaseUri + endPoint;
        SendOutlierDto sendOutlierDto = new SendOutlierDto(devEui
                , sensorName
                , topicDto.getPlace()
                , topicDto.getType()
                , mqttModbusDTO.getTime()
                , mqttModbusDTO.getValue()
                , 1);

        clientAdaptor.sendPostRequestFront(url,sendOutlierDto);
    }

    @Override
    public void sendOutlierToApi(String endPoint, TopicDto topicDto, MqttModbusDTO mqttModbusDTO) {
        String url = apiBaseUri + endPoint;
        SaveOutlierDto saveOutlierDto = new SaveOutlierDto(topicDto.getPlace()
                , topicDto.getType()
                , mqttModbusDTO.getTime()
                , mqttModbusDTO.getValue()
                , 1
                , 0);

        clientAdaptor.sendPostRequest(url,saveOutlierDto);
    }

    @Override
    public void lightControl(String sensorName,String flag) {
        String url = String.format(controlUrlFormat, controlBaseUri,flag ,sensorName, devEui);
        clientAdaptor.sendGetRequest(url);
    }

}
