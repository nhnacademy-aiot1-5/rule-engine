package live.ioteatime.ruleengine.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitClientConfig {
    @Value("${my.rabbitmq.host}")
    private String host;
    @Value("${my.rabbitmq.port}")
    private String port;
    @Value("${my.rabbitmq.username}")
    private String username;
    @Value("${my.rabbitmq.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ConnectionFactory();
    }
}
