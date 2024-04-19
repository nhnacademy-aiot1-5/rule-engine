package live.ioteatime.ruleengine.rule;

import live.ioteatime.ruleengine.domain.MqttData;

public interface Rule {

    void doProcess(MqttData mqttData, RuleChain ruleChain);
}
