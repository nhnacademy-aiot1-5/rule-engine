package live.ioteatime.ruleengine.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChannelDto {
    private int channelId;
    private int sensorId;
    private int placeId;
    private String channelName;
    private int address;
    private int quantity;
    private int functionCode;
}
