package live.ioteatime.ruleengine.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import live.ioteatime.ruleengine.domain.MqttData;
import live.ioteatime.ruleengine.rule.Rule;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RuleConfig {
    @Bean
    public Rule nullCheck() {
        return ((mqttData, ruleChain) -> {
            Result result = getResult(mqttData);

            if (mqttData.getValue() == null) {
                return;
            }
            if (mqttData.getValue().equals(0.0f)){
                if (result.type.equals("temperature")) {
                    ruleChain.doProcess(mqttData);
                }
                return;
            }
            ruleChain.doProcess(mqttData);
        });
    }

    @Bean
    public Rule inputInflux(InfluxDBClient influxDBClient) {
        return ((mqttData, ruleChain) -> {
            Result result = getResult(mqttData);
            WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
            Point point = Point.measurement("test-measurement")
                    .time(mqttData.getTime(), WritePrecision.MS)
                    .addTag("topic", mqttData.getTopic())
                    .addTag("place", result.place)
                    .addTag("type", result.type)
                    .addTag("phase", result.phase)
                    .addTag("description", result.description)
                    .addField("value", mqttData.getValue());
            writeApiBlocking.writePoint(point);

            ruleChain.doProcess(mqttData);
        });
    }

    private static @NotNull Result getResult(MqttData mqttData) {
        String[] tags = mqttData.getTopic().split("/");
        String place = tags[6];
        String type = tags[12];
        String phase = tags[14];
        String description = tags[16];
        return new Result(place, type, phase, description);
    }

    private static class Result {
        public final String place;
        public final String type;
        public final String phase;
        public final String description;

        public Result(String place, String type, String phase, String description) {
            this.place = place;
            this.type = type;
            this.phase = phase;
            this.description = description;
        }
    }
}
