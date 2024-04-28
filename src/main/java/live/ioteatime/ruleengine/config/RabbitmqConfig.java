package live.ioteatime.ruleengine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import live.ioteatime.ruleengine.domain.MqttData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RabbitmqConfig {
    private final BlockingQueue<MqttData> blockingQueue;
    private final ObjectMapper mapper;
    private final ConnectionFactory connectionFactory;

    /**
     * Rabbitmq 에서 데이터들을 불러와
     * blockingQueue 에 넣는 메소드 입니다.
     */
    @Bean
    public Connection getMessage() throws IOException, TimeoutException {
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        log.info("RabbitMQ connection established");
        String queueName = "MessageQueue";
        channel.queueDeclare(queueName, true, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            MqttData data = mapper.readValue(message, MqttData.class);
                try {
                    blockingQueue.put(data);
                } catch (InterruptedException e) {
                    log.error("Blocking Queue Error : {}",e.getMessage());
                    Thread.currentThread().interrupt();
                }
        };
            // 컨슈머가 메시지를 소비하는 방법
            // queueName = 가져올 큐 이름
            // true = 자동으로 ack 전(작업 완료 신호인 ack가 rabbitmq에 도착해야 큐에서 데이터 삭제)
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

        return connection;
        }

}
