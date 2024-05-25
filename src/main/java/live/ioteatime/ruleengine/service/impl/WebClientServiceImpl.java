package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;
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

    @Override
    public void sendOutlier(String endPoint, TopicDto topicDto, MqttModbusDTO mqttModbusDTO) {
        String url = apiBaseUri + endPoint;
        SendOutlierDto sendOutlierDto = new SendOutlierDto(topicDto.getPlace()
                , topicDto.getType()
                , mqttModbusDTO.getTime()
                , mqttModbusDTO.getValue());

        webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sendOutlierDto), SendOutlierDto.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> log.info("response {}", response)
                        , error -> log.error("send outlier error", error)
                );
    }

    @Override
    public void setRedLightSignal(String sensorName) {
        String url = String.format("%s/sensor/on?sensorName=%s&devEui=%s",controlBaseUri ,sensorName, devEui);

        webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> log.info("setReadLight response {}", response)
                        , error -> log.error("setReadLight error", error)
                );
    }

}
