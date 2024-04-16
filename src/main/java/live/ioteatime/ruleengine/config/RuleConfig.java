package live.ioteatime.ruleengine.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import live.ioteatime.ruleengine.rule.Rule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RuleConfig {
    // TODO 룰 구현
    @Bean
    public Rule nullCheck() {
        return ((mqttData, ruleChain) -> {
            if (mqttData.getValue() == null || (mqttData.getValue().equals(0.0f))) {
                return;
            }
            ruleChain.doProcess(mqttData);
        });
    }

    @Bean
    public Rule inputInflux(InfluxDBClient influxDBClient) {
        return ((mqttData, ruleChain) -> {
            WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
            Point point = Point.measurement("test-measurement")
                    .time(mqttData.getTime(), WritePrecision.MS)
                    .addTag("topic", mqttData.getTopic())
                    .addField("value", mqttData.getValue());
            writeApiBlocking.writePoint(point);

            ruleChain.doProcess(mqttData);
        });
    }
}
