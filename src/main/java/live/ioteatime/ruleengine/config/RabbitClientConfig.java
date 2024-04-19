package live.ioteatime.ruleengine.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitClientConfig {
    @Value("${Spring.rabbitmq.host}")
    private String host;
    @Value("${Spring.rabbitmq.port}")
    private String port;
    @Value("${Spring.rabbitmq.username}")
    private String username;
    @Value("${Spring.rabbitmq.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ConnectionFactory();
    }
}
