package live.ioteatime.ruleengine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import live.ioteatime.ruleengine.domain.MqttData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public BlockingQueue<MqttData> blockingQueue() {

        return new LinkedBlockingQueue<>();
    }

    @Bean
    public ObjectMapper objectMapper() {

        return new ObjectMapper();
    }
}
