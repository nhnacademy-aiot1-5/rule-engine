package live.ioteatime.ruleengine.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.domain.Outlier;
import live.ioteatime.ruleengine.domain.TopicDto;
import live.ioteatime.ruleengine.properties.InfluxDBProperties;
import live.ioteatime.ruleengine.rule.Rule;
import live.ioteatime.ruleengine.rule.RuleChain;
import live.ioteatime.ruleengine.service.MappingTableService;
import live.ioteatime.ruleengine.service.OutlierService;
import live.ioteatime.ruleengine.service.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RuleConfig {
    private final OutlierService outlierService;
    private final MappingTableService mappingTableService;
    private final WebClientService webClientService;
    private final InfluxDBProperties influxDBProperties;
    private static final String DESCRIPTION = "w";

    private enum Protocol {
        MODBUS, MQTT
    }

    private boolean outlierCheck = false;

    @Bean
    public Rule acRule() {
        return ((mqttModbusDTO, ruleChain) -> {
            if (mqttModbusDTO.getId().contains("sensor")) {
                log.info("ac role");
            }
        });
    }

    @Bean
    public Rule nullCheck() {
        return ((mqttModbusDTO, ruleChain) -> {
            if (Protocol.MODBUS.toString().equals(mqttModbusDTO.getProtocol())) {
                ruleChain.doProcess(mqttModbusDTO);
                return;
            }
            TopicDto topicDto = splitTopic(mqttModbusDTO);

            if (mqttModbusDTO.getValue() == null) {
                return;
            }
            if (!(mqttModbusDTO.getValue().equals(0.0f) && topicDto.getType().equals("temperature"))) {
                return;
            }
            ruleChain.doProcess(mqttModbusDTO);
        });
    }

    @Bean
    public Rule outlierCheck() {
        return ((mqttModbusDTO, ruleChain) -> {
            TopicDto topicDto = splitTopic(mqttModbusDTO);

            if (!outlierService.checkOutlier(topicDto.getPlace())) return;

            if (!DESCRIPTION.equals(topicDto.getDescription())) {
                ruleChain.doProcess(mqttModbusDTO);

                return;
            }
            MinMaxDto minMaxDto = outlierService.getMinMax(topicDto.getPlace());

            if (mqttModbusDTO.getValue() < minMaxDto.getMin() || mqttModbusDTO.getValue() > minMaxDto.getMax()) {
                outlierCheck = true;
                log.error("outlier! place : {}, description : {}, value : {} ", topicDto.getPlace(), topicDto.getDescription(), mqttModbusDTO.getValue());

                webClientService.setRedLightSignal(Outlier.LIGHT.getLowercase());
                webClientService.sendOutlierToApi("/api", topicDto, mqttModbusDTO);
                webClientService.sendOutlierToFront("/outlier", topicDto, mqttModbusDTO,Outlier.LIGHT.getLowercase());
            }
            ruleChain.doProcess(mqttModbusDTO);
        });
    }

    @Bean
    public Rule inputInflux(InfluxDBClient influxDBClient) {
        return ((mqttModbusDTO, ruleChain) -> {
            if (String.valueOf(Protocol.MQTT).equals(mqttModbusDTO.getProtocol())) {
                insertData(influxDBClient, mqttModbusDTO, ruleChain, outlierCheck,true);

                return;
            }
            insertData(influxDBClient, mqttModbusDTO, ruleChain, outlierCheck,false);
        });
    }

    private void insertData(InfluxDBClient influxDBClient, MqttModbusDTO mqttModbusDTO, RuleChain ruleChain, boolean isOutlier,boolean isMqtt) {
        String bucket = isOutlier ? influxDBProperties.getOutlier() : influxDBProperties.getMain();
        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
        Point point = buildPoint(mqttModbusDTO, isMqtt);

        writeApiBlocking.writePoint(bucket, influxDBProperties.getOrg(), point);
        outlierCheck = false;
        ruleChain.doProcess(mqttModbusDTO);
    }


    private Point buildPoint(MqttModbusDTO mqttModbusDTO, boolean isMqtt) {
        if (isMqtt) {
            TopicDto topicDto = splitTopic(mqttModbusDTO);
            return Point.measurement("test-measurement")
                    .time(mqttModbusDTO.getTime(), WritePrecision.MS)
                    .addTag("topic", mqttModbusDTO.getId())
                    .addTag("place", topicDto.getPlace())
                    .addTag("type", topicDto.getType())
                    .addTag("phase", topicDto.getPhase())
                    .addTag("description", topicDto.getDescription())
                    .addField("value", mqttModbusDTO.getValue());
        } else {
            String address = mqttModbusDTO.getId().split("/")[1];
            Map<String, String> tags = mappingTableService.getTags(Integer.parseInt(address));
            return Point.measurement("test-measurement")
                    .time(mqttModbusDTO.getTime(), WritePrecision.MS)
                    .addTags(tags)
                    .addField("value", mqttModbusDTO.getValue());
        }
    }

    private @NotNull TopicDto splitTopic(MqttModbusDTO mqttModbusDTO) {
        String[] tags = mqttModbusDTO.getId().split("/");
        String place = tags[6];
        String type = tags[12];
        String phase = tags[14];
        String description = tags[16];

        return new TopicDto(place, type, phase, description);
    }

}
