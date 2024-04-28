package live.ioteatime.ruleengine.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MqttInfo {
    private String mqttHost;
    private String mqttId;
    private String mqttTopic;
}
