package live.ioteatime.ruleengine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.influxdb.client.write.Point;
import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class CommonConfig {

    @Bean
    public BlockingQueue<MqttModbusDTO> blockingQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

    @Bean
    public List<Point> points() {
        return new ArrayList<>();
    }
}
