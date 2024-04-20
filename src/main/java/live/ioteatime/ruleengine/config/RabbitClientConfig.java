package live.ioteatime.ruleengine.config;

import com.rabbitmq.client.ConnectionFactory;
import live.ioteatime.ruleengine.properties.RabbitmqProperties;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitClientConfig {

    @Bean
    public ConnectionFactory connectionFactory(RabbitmqProperties rabbitmqProperties) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitmqProperties.getHost());
        connectionFactory.setUsername(rabbitmqProperties.getUsername());
        connectionFactory.setPassword(rabbitmqProperties.getPassword());
        connectionFactory.setPort(Integer.parseInt(rabbitmqProperties.getPort()));

        return connectionFactory;
    }
}
