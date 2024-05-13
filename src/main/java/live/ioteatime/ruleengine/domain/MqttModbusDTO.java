package live.ioteatime.ruleengine.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MqttModbusDTO {
    @Getter
    private String protocol;
    @Setter
    private String id;
    private Long time;
    private Float value;
}
