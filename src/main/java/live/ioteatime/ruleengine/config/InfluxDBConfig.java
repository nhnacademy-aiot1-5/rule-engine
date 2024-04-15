package live.ioteatime.ruleengine.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import live.ioteatime.ruleengine.properties.InfluxDBProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {

    @Bean
    public InfluxDBClient influxDBClient(InfluxDBProperties influxDBProperties) {

        return InfluxDBClientFactory.create(
            influxDBProperties.getUrl(),
            influxDBProperties.getToken().toCharArray(),
            influxDBProperties.getOrg(),
            influxDBProperties.getBucket());
    }
}
