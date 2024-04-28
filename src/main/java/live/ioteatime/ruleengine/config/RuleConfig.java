package live.ioteatime.ruleengine.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import live.ioteatime.ruleengine.domain.MqttData;
import live.ioteatime.ruleengine.domain.TopicDto;
import live.ioteatime.ruleengine.rule.Rule;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RuleConfig {
    @Bean
    public Rule nullCheck() {
        return ((mqttData, ruleChain) -> {
            TopicDto topicDto = getResult(mqttData);

            if (mqttData.getValue() == null) {

                return;
            }

            if (mqttData.getValue().equals(0.0f)) {
                if (topicDto.getType().equals("temperature")) {
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
            TopicDto topicDto = getResult(mqttData);
            WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
            Point point = Point.measurement("test-measurement")
                    .time(mqttData.getTime(), WritePrecision.MS)
                    .addTag("topic", mqttData.getTopic())
                    .addTag("place", topicDto.getPlace())
                    .addTag("type", topicDto.getType())
                    .addTag("phase", topicDto.getPhase())
                    .addTag("description", topicDto.getDescription())
                    .addField("value", mqttData.getValue());

            writeApiBlocking.writePoint(point);

            ruleChain.doProcess(mqttData);
        });
    }

    private static @NotNull TopicDto getResult(MqttData mqttData) {
        String[] tags = mqttData.getTopic().split("/");
        String place = tags[6];
        String type = tags[12];
        String phase = tags[14];
        String description = tags[16];

        return new TopicDto(place, type, phase, description);
    }

}
