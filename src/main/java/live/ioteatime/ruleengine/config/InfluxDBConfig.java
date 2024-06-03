package live.ioteatime.ruleengine.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import live.ioteatime.ruleengine.properties.InfluxDBProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class InfluxDBConfig {

    private static final String LOGGING_CONNECT = "InfluxDBClient connect success: {}";

    @Bean
    public InfluxDBClient influxDBClient(InfluxDBProperties influxDBProperties) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(
                influxDBProperties.getUrl(),
                influxDBProperties.getToken().toCharArray());
        log.info(LOGGING_CONNECT, influxDBProperties.getUrl());

        return influxDBClient;
    }

    @Bean
    public WriteApiBlocking writeApiBlocking(InfluxDBClient influxDBClient) {
        return influxDBClient.getWriteApiBlocking();
    }
}
