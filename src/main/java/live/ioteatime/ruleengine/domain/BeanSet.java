package live.ioteatime.ruleengine.domain;

import lombok.Data;

@Data
public class BeanSet {

    private String mqttHost;
    private String mqttId;
    private String mqttTopic;
    private String rabbitmqHost;
    private String rabbitmqPort;
    private String rabbitmqUsername;
    private String rabbitmqPassword;
    private String rabbitmqExchange;
    private String rabbitmqRoutingKey;
}
