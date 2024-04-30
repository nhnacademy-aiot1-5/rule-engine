package live.ioteatime.ruleengine.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "daily_elec")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPower {
    @Id
    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "value")
    private Double value;

}