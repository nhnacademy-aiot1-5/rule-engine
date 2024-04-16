package live.ioteatime.ruleengine.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class MqttData {

    @Setter
    private String topic;
    private Long time;
    private Float value;
}
