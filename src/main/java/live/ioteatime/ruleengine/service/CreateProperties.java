package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.ModbusInfo;
import live.ioteatime.ruleengine.domain.MqttInfo;

public interface CreateProperties {

    /**
     *  mqtt bridge 생성 메소드
     * @param mqttInfo 브릿지 생성을 위한 데이터
     * @return 만들어진 설정파일 경로
     */
    String createConfig(MqttInfo mqttInfo);

    /**
     *  modbus bridge 생성 메소드
     * @param modbus 브릿지 생성을 위한 데이터
     * @return 먼들어진 설정파일 경로
     */
    String createConfig(ModbusInfo modbus);
}
