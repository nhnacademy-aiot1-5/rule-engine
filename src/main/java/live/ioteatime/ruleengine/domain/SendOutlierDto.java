package live.ioteatime.ruleengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SendOutlierDto {
    private String place;
    private String type;
    private long time;
    private float value;
}
