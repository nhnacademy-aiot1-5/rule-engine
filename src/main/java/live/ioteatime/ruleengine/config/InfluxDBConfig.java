package live.ioteatime.ruleengine.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import live.ioteatime.ruleengine.properties.InfluxDBProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class InfluxDBConfig {

    @Bean
    public InfluxDBClient influxDBClient(InfluxDBProperties influxDBProperties) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(
            influxDBProperties.getUrl(),
            influxDBProperties.getToken().toCharArray(),
            influxDBProperties.getOrg(),
            influxDBProperties.getBucket());
        log.info("InfluxDBClient connect success: {}", influxDBProperties.getUrl());

        return influxDBClient;
    }

    @Bean
    public QueryApi queryApi(InfluxDBClient influxDBClient) {

        return influxDBClient.getQueryApi();
    }

}
