package live.ioteatime.ruleengine.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mqtt")
@Getter
@Setter
public class MqttClientProperties {

    private String uri;
    private String id;
    private List<String> topics;
}
