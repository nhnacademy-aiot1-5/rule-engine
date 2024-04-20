package live.ioteatime.ruleengine.config;

import com.rabbitmq.client.ConnectionFactory;
import live.ioteatime.ruleengine.properties.RabbitmqInfo;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitClientConfig {

    @Bean
    public ConnectionFactory connectionFactory(RabbitmqInfo rabbitmqInfo) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitmqInfo.getHost());
        connectionFactory.setUsername(rabbitmqInfo.getUsername());
        connectionFactory.setPassword(rabbitmqInfo.getPassword());
        connectionFactory.setPort(Integer.parseInt(rabbitmqInfo.getPort()));

        return connectionFactory;
    }
}
