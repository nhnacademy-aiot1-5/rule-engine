package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.domain.SendOutlierDto;
import live.ioteatime.ruleengine.domain.TopicDto;
import live.ioteatime.ruleengine.service.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientServiceImpl implements WebClientService {
    private final WebClient webClient;

    @Override
    public void sendOutlier(TopicDto topicDto, MqttModbusDTO mqttModbusDTO) {
        String url = "/outlier";
        SendOutlierDto sendOutlierDto = new SendOutlierDto(topicDto.getPlace()
                ,topicDto.getType()
                ,mqttModbusDTO.getTime()
                ,mqttModbusDTO.getValue());

        webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sendOutlierDto), SendOutlierDto.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> log.info("response {}", response)
                        , error -> log.error("send outlier error",error)
                );
    }

}
