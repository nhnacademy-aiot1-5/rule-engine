package live.ioteatime.ruleengine.config;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.domain.Outlier;
import live.ioteatime.ruleengine.domain.TopicDto;
import live.ioteatime.ruleengine.rule.Rule;
import live.ioteatime.ruleengine.rule.RuleChain;
import live.ioteatime.ruleengine.service.MappingTableService;
import live.ioteatime.ruleengine.service.OutlierService;
import live.ioteatime.ruleengine.service.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RuleConfig {

    @Value("${front.outlier.endpoint}")
    private String frontEndpoint;

    @Value("${api.endpoint}")
    private String apiEndpoint;

    @Value("${outlier.description}")
    private String outlierDesc;

    @Value("${sensor.flag}")
    private String sensorFlag;

    @Value("${sensor.occupancy}")
    private String sensorOccupancy;

    @Value("${outlier.type}")
    private String outlierType;

    @Value("${outlier.phase}")
    private String outlierPhase;

    @Value("${dooray.endpoint}")
    private String doorayEndpoint;

    private static final String LOGGING_LIGHT_OFF = "light control off";
    private static final String LOGGING_LIGHT_ON = "light control on";
    private static final String LOGGING_OUTLIER = "outlier! place : {}, type {} ,description : {}, value : {} , phase {}";
    private static final String ZERO_TYPE = "temperature";
    private static final String LOGGING_OUTLIER_STATUE = "empty outlier!";
    private static final String LOGGING_OUTLIER_TRACE = "in outlierCheck place {} | type {} | description {} | value {} | phase {}";

    private final OutlierService outlierService;
    private final MappingTableService mappingTableService;
    private final WebClientService webClientService;
    private final List<Point> points;

    private enum Protocol {
        MODBUS, MQTT
    }

    @Bean
    public Rule acRule() {
        return ((mqttModbusDTO, ruleChain) -> {
            if (mqttModbusDTO.getId().contains(sensorOccupancy)) {
                if (mqttModbusDTO.getValue() == 0) {
                    webClientService.lightControl(Outlier.AC.getLowercase(), "off");
                    loggingLightState(LOGGING_LIGHT_OFF);
                    return;
                }
                if (mqttModbusDTO.getValue() == 1) {
                    webClientService.lightControl(Outlier.AC.getLowercase(), "on");
                    loggingLightState(LOGGING_LIGHT_ON);
                    return;
                }
                return;
            }
            ruleChain.doProcess(mqttModbusDTO);
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
            if ((!topicDto.getType().equals(ZERO_TYPE)) && mqttModbusDTO.getValue().equals(0.0f)) {
                return;
            }
            ruleChain.doProcess(mqttModbusDTO);
        });
    }

    @Bean
    public Rule outlierCheck() {
        return ((mqttModbusDTO, ruleChain) -> {
            TopicDto topicDto = splitTopic(mqttModbusDTO);

            if (!outlierService.checkOutlier(topicDto.getPlace())) {
                ruleChain.doProcess(mqttModbusDTO);
                return;
            }

            if (!outlierDesc.equals(topicDto.getDescription())) {
                ruleChain.doProcess(mqttModbusDTO);
                return;
            }

            MinMaxDto minMaxDto = outlierService.getMinMax(topicDto.getPlace());
            if (minMaxDto == null) {
                log.error(LOGGING_OUTLIER_STATUE);
                ruleChain.doProcess(mqttModbusDTO);
                return;
            }

            if (!(outlierType.equals(topicDto.getType()) && outlierPhase.equals(topicDto.getPhase()))) {
                ruleChain.doProcess(mqttModbusDTO);
                return;
            }

            log.debug(LOGGING_OUTLIER_TRACE, topicDto.getPlace()
                    , topicDto.getType()
                    , topicDto.getDescription()
                    , mqttModbusDTO.getValue()
                    , topicDto.getPhase());

            if (mqttModbusDTO.getValue() < minMaxDto.getMin() || mqttModbusDTO.getValue() > minMaxDto.getMax()) {
                log.warn(LOGGING_OUTLIER
                        , topicDto.getPlace()
                        , topicDto.getType()
                        , topicDto.getDescription()
                        , mqttModbusDTO.getValue()
                        , topicDto.getPhase());

                webClientService.lightControl(Outlier.LIGHT.getLowercase(), sensorFlag);
                webClientService.sendDooray(doorayEndpoint, topicDto, mqttModbusDTO);
                webClientService.sendOutlierToApi(apiEndpoint, topicDto, mqttModbusDTO);
                webClientService.sendOutlierToFront(frontEndpoint, topicDto, mqttModbusDTO, Outlier.LIGHT.getLowercase());

                ruleChain.doProcess(mqttModbusDTO);
                return;
            }
            ruleChain.doProcess(mqttModbusDTO);
        });
    }

    @Bean
    public Rule inputInflux() {
        return ((mqttModbusDTO, ruleChain) -> {
            if (String.valueOf(Protocol.MQTT).equals(mqttModbusDTO.getProtocol())) {
                insertData(mqttModbusDTO, ruleChain, true);
                return;
            }
            insertData(mqttModbusDTO, ruleChain, false);
        });
    }

    private void insertData(MqttModbusDTO mqttModbusDTO, RuleChain ruleChain, boolean isMqtt) {
        Point point = buildPoint(mqttModbusDTO, isMqtt);

        points.add(point);

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

    private static void loggingLightState(String message) {
        log.info(message);
    }
}
