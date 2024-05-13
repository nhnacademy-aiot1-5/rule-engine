package live.ioteatime.ruleengine.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "channels")
public class ChannelEntity {
    @Id
    private int channelId;
    private int sensorId;
    private int placeId;
    private String channelName;
    private int address;
    private int type;
    private int functionCode;
}
