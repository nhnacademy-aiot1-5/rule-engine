package live.ioteatime.ruleengine.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SaveOutlierDto {
    private String place;
    private String type;
    private long time;
    private double outlierValue;
    private int organizationId;
    private int flag;
}
