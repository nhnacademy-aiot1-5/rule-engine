package live.ioteatime.ruleengine.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MqttInfo {
    private String mqttHost;
    private String mqttId;
    private List<String> mqttTopic;
}
