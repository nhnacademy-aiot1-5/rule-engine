package live.ioteatime.ruleengine.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Tags {
    private String place;
    private String type;
    private String phase;
    private String description;
}
