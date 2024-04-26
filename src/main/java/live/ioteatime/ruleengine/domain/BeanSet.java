package live.ioteatime.ruleengine.domain;

import lombok.Data;

@Data
public class BeanSet {

    private String mqttHost;
    private String mqttId;
    private String mqttTopic;
}
