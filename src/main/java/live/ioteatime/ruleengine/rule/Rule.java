package live.ioteatime.ruleengine.rule;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;

public interface Rule {

    void doProcess(MqttModbusDTO mqttModbusDTO, RuleChain ruleChain);
}
