package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.domain.SaveOutlierDto;
import live.ioteatime.ruleengine.domain.SendOutlierDto;
import live.ioteatime.ruleengine.domain.TopicDto;
import live.ioteatime.ruleengine.service.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientServiceImpl implements WebClientService {
    private final WebClient webClient;
    @Value("${control.dev.eui}")
    private String devEui;
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

        sendPostRequest(url,sendOutlierDto);
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

        sendPostRequest(url,saveOutlierDto);
    }

    @Override
    public void setRedLightSignal(String sensorName) {
        String url = String.format("%s/sensor/on?sensorName=%s&devEui=%s", controlBaseUri, sensorName, devEui);

        sendGetRequest(url);
    }

    private void sendPostRequest(String url, Object body) {
        webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), body.getClass())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info("post response: {}", response),
                        error -> log.error("send post request error", error)
                );
    }

    private void sendGetRequest(String url) {
        webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info("get response: {}", response),
                        error -> log.error("send get request error", error)
                );
    }

}
