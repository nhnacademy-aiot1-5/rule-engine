package live.ioteatime.ruleengine.util;

import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@UtilityClass
public class TimeUtils {

    /**
     * 시간 앞 2자리만 분리, ex) 17:52:..:.. -> 17
     *
     * @return 날짜 , 시각
     */
    public static LocalDateTimeDto localDateTime() {
        int time = Integer.parseInt(LocalTime.now().toString().split(":")[0]);
        LocalDate localDate = LocalDate.now();

        log.info("time: {}", time);
        log.info("localDate: {}", localDate);

        return new LocalDateTimeDto(localDate, time);
    }

}
