package live.ioteatime.ruleengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LocalDateTimeDto {

    private LocalDate date;

    private int time;
}
