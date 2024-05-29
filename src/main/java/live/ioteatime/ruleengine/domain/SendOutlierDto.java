package live.ioteatime.ruleengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SendOutlierDto {
    private String deveui;
    private String sensorName;
    private String place;
    private String type;
    private long time;
    private double outlierValue;
    private int organizationId;
}
