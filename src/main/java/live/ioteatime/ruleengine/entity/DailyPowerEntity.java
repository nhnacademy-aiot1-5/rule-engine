package live.ioteatime.ruleengine.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "daily_electricity_consumption")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyPowerEntity {
    @Id
    @Column(name = "time")
    private LocalDateTime time;
    private int channelId;
    private Double kwh;
    private Double bill;
}
