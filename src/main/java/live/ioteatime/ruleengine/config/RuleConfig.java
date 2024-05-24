package live.ioteatime.ruleengine.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.domain.Outlier;
import live.ioteatime.ruleengine.domain.TopicDto;
import live.ioteatime.ruleengine.entity.ControllerStatusEntity;
import live.ioteatime.ruleengine.properties.InfluxDBProperties;
import live.ioteatime.ruleengine.repository.ControllerStatusRepository;
import live.ioteatime.ruleengine.repository.impl.OutlierRepository;
import live.ioteatime.ruleengine.rule.Rule;
import live.ioteatime.ruleengine.rule.RuleChain;
import live.ioteatime.ruleengine.service.MappingTableService;
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
    private final OutlierRepository outlierRepository;
    private final MappingTableService mappingTableService;
    private final ControllerStatusRepository controllerStatusRepository;
    private final WebClientService webClientService;
    private final InfluxDBProperties influxDBProperties;
    private static final String DESCRIPTION = "w";

    private enum Protocol {
        MODBUS, MQTT
    }

    private boolean isOutlier = false;

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
            if (mqttModbusDTO.getValue().equals(0.0f)) {
                if (topicDto.getType().equals("temperature")) {
                    ruleChain.doProcess(mqttModbusDTO);
                }
                return;
            }
            ruleChain.doProcess(mqttModbusDTO);
        });
    }

    @Bean
    public Rule outlierCheck() {
        return ((mqttModbusDTO, ruleChain) -> {
            TopicDto topicDto = splitTopic(mqttModbusDTO);

            if (!outlierRepository.getKeys().contains(topicDto.getPlace())) return;

            if (!topicDto.getDescription().equals(DESCRIPTION)) {
                ruleChain.doProcess(mqttModbusDTO);

                return;
            }
            MinMaxDto minMaxDto = outlierRepository.getOutliers().get(topicDto.getPlace());

            if (mqttModbusDTO.getValue() < minMaxDto.getMin() || mqttModbusDTO.getValue() > minMaxDto.getMax()) {
                isOutlier = true;
                log.error("outlier! place : {}, description : {}, value : {} ", topicDto.getPlace(), topicDto.getDescription(), mqttModbusDTO.getValue());
                webClientService.setRedLightSignal(Outlier.LIGHT.getLowercase());
                updateStatus(Outlier.LIGHT.getId());
                webClientService.sendOutlier("/outlier", topicDto, mqttModbusDTO);
            }

            ruleChain.doProcess(mqttModbusDTO);
        });
    }

    @Bean
    public Rule inputInflux(InfluxDBClient influxDBClient) {
        return ((mqttModbusDTO, ruleChain) -> {
            if (String.valueOf(Protocol.MQTT).equals(mqttModbusDTO.getProtocol())) {
                insertMqtt(influxDBClient, mqttModbusDTO, ruleChain, isOutlier);
                isOutlier = false;

                return;
            }
            insertModbus(influxDBClient, mqttModbusDTO, ruleChain, isOutlier);
            isOutlier = false;
        });
    }

    private void insertMqtt(InfluxDBClient influxDBClient, MqttModbusDTO mqttModbusDTO, RuleChain ruleChain, boolean isOutlier) {
        TopicDto topicDto = splitTopic(mqttModbusDTO);
        String bucket = isOutlier ? influxDBProperties.getOutlier() : influxDBProperties.getMain();
        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();

        Point point = Point.measurement("test-measurement")
                .time(mqttModbusDTO.getTime(), WritePrecision.MS)
                .addTag("topic", mqttModbusDTO.getId())
                .addTag("place", topicDto.getPlace())
                .addTag("type", topicDto.getType())
                .addTag("phase", topicDto.getPhase())
                .addTag("description", topicDto.getDescription())
                .addField("value", mqttModbusDTO.getValue());

        writeApiBlocking.writePoint(bucket, influxDBProperties.getOrg(), point);

        ruleChain.doProcess(mqttModbusDTO);
    }

    private void insertModbus(InfluxDBClient influxDBClient, MqttModbusDTO mqttModbusDTO, RuleChain ruleChain, boolean isOutlier) {
        String bucket = isOutlier ? influxDBProperties.getOutlier() : influxDBProperties.getMain();
        String address = mqttModbusDTO.getId().split("/")[1];
        Map<String, String> tags = mappingTableService.getTags(Integer.parseInt(address));
        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();

        Point point = Point.measurement("test-measurement")
                .time(mqttModbusDTO.getTime(), WritePrecision.MS)
                .addTags(tags)
                .addField("value", mqttModbusDTO.getValue());

        writeApiBlocking.writePoint(bucket, influxDBProperties.getOrg(), point);

        ruleChain.doProcess(mqttModbusDTO);
    }

    private @NotNull TopicDto splitTopic(MqttModbusDTO mqttModbusDTO) {
        String[] tags = mqttModbusDTO.getId().split("/");
        String place = tags[6];
        String type = tags[12];
        String phase = tags[14];
        String description = tags[16];

        return new TopicDto(place, type, phase, description);
    }

    private void updateStatus(String id) {
        ControllerStatusEntity status = controllerStatusRepository.findByControllerId(id);
        if (status == null) {
            throw new NullPointerException("status is null");
        }
        status.setStatus(1);

        controllerStatusRepository.save(status);
    }

}
