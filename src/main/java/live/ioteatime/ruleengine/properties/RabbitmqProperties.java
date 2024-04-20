package live.ioteatime.ruleengine.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq")
@Getter
@Setter
public class RabbitmqProperties {
    private String host;
   private String port;
    private String username;
    private String password;
}
