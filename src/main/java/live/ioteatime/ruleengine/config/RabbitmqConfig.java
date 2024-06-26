package live.ioteatime.ruleengine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RabbitmqConfig {

    @Value("${rabbitmq.queue}")
    private String queue;

    private static final String LOGGING_CONNECT = "RabbitMQ connection established";
    private static final String LOGGING_QUEUE_ERROR = "Blocking Queue Error : {}";

    private final BlockingQueue<MqttModbusDTO> blockingQueue;
    private final ObjectMapper mapper;
    private final ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;

    @PreDestroy
    public void closeConnection() throws IOException, TimeoutException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }

    /**
     * Rabbitmq 에서 데이터들을 불러와
     * blockingQueue 에 넣는 메소드 입니다.
     */
    @Bean
    public Connection getMessage() throws IOException, TimeoutException {
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        log.info(LOGGING_CONNECT);
        channel.queueDeclare(queue, true, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            MqttModbusDTO data = mapper.readValue(message, MqttModbusDTO.class);
            try {
                blockingQueue.put(data);
            } catch (InterruptedException e) {
                log.error(LOGGING_QUEUE_ERROR, e.getMessage());
                Thread.currentThread().interrupt();
            }
        };

        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});

        return connection;
    }
}
