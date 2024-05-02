package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.MqttInfo;

public interface CreateProperties {

    String createConfig(MqttInfo mqttInfo);
}
