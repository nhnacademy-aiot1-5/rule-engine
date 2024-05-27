package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.domain.TopicDto;

public interface WebClientService {

    /**
     * 이상치 감지 시 front-service 에 이상치를 전송 하는 메소드
     * @param endPoint 보낼 엔드포인트
     * @param topicDto 장소,파입 등등
     * @param mqttModbusDTO 감지된 데이터
     * @param sensorName 조절 할 대상 센서
     */
    void sendOutlierToFront(String endPoint,TopicDto topicDto, MqttModbusDTO mqttModbusDTO,String sensorName);

    /**
     * 이상치 감지 시 api-service 에 이상치를 전송 하는 메소드
     * @param endPoint 보낼 엔드포인트
     * @param topicDto 장소,파입 등등
     * @param mqttModbusDTO 감지된 데이터
     */
    void sendOutlierToApi(String endPoint,TopicDto topicDto, MqttModbusDTO mqttModbusDTO);

    /**
     * 이상치 감지 시 control 서버에 신호를 보내는 메소드
     * @param sensorName light(경광등)
     */
    void setRedLightSignal(String sensorName);

    /**
     *  재실센서가 사람 있으면 에어컨(노란 불) 켜짐
     * @param sensorName 센서 이름
     */
    void setYellowLightSignal(String sensorName);

    /**
     *  재실센서가 사람 없으면 에어컨(노란 불) 꺼짐
     * @param sensorName 센서 이름
     */
    void offYellowLightSignal(String sensorName);
}
