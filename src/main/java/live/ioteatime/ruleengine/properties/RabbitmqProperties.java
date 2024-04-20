package live.ioteatime.ruleengine.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
public class RabbitmqProperties {
    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.port}")
   private int port;
    @Value("${rabbitmq.username}")
    private String username;
    @Value("${rabbitmq.password}")
    private String password;
}
