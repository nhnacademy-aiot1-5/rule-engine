package live.ioteatime.ruleengine.rule;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;

public interface RuleChain {

    void doProcess(MqttModbusDTO mqttModbusDTO);
    void resetThreadLocal();
    void clearThreadLocal();
}
