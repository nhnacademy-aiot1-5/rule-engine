package live.ioteatime.ruleengine.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class RabbitmqInfo {
    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.port}")
   private String port;
    @Value("${rabbitmq.username}")
    private String username;
    @Value("${rabbitmq.password}")
    private String password;
}
