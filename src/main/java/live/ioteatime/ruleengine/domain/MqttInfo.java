package live.ioteatime.ruleengine.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Schema(description = "MQTT 정보")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MqttInfo {

    @Schema(description = "MQTT HOST",example = "tcp://host:port")
    private String mqttHost;

    @Schema(description = "MQTT ID",example = "clientId")
    private String mqttId;

    @Schema(description = "MQTT TOPICS",example = "[\"topic\", \"topic\"]")
    private List<String> mqttTopic;
}
