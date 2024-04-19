package live.ioteatime.ruleengine.rule;

import live.ioteatime.ruleengine.domain.MqttData;

public interface RuleChain {

    void doProcess(MqttData mqttData);

    void resetThreadLocal();
}
