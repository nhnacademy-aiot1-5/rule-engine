package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.domain.TopicDto;

public interface WebClientService {

    /**
     * 이상치 감지 시 api 서버에 요청을 보내는 메소드
     * @param topicDto 장소,타입 등
     * @param mqttModbusDTO 시간, 값
     */
    void sendOutlier(TopicDto topicDto, MqttModbusDTO mqttModbusDTO);
}
