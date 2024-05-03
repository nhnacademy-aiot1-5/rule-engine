package live.ioteatime.ruleengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class LocalDateTimeDto {
    private LocalDate date;
    private int time;
}
