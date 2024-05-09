package live.ioteatime.ruleengine.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import live.ioteatime.ruleengine.domain.*;
import live.ioteatime.ruleengine.service.OutlierService;
import live.ioteatime.ruleengine.service.impl.DailyPowerServiceImpl;
import live.ioteatime.ruleengine.service.impl.QueryServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportScheduler {
    private final DailyPowerServiceImpl dailyPowerService;
    private final QueryServiceImpl queryServiceImpl;
    private final OutlierService outlierService;
    private final OutlierRepo outlierRepo;
    @Value("${schedule.flag}")
    private boolean cronFlag;
    LocalMidnightDto localMidnightDto = new LocalMidnightDto();

    @Scheduled(cron = "${schedule.cron1}")
    public void saveTodayPower() {
        if (cronFlag) {
             createMidnight();

            for (int i = 0; i < dailyPowerService.getQueryCount(); i++) {
                Map<LocalDateTime, Object> queryResult = dailyPowerService.convertInfluxDbResponseToLocalTime(dailyPowerService.getQuery(i));
                int channelId = queryServiceImpl.getChannelId(i);
                double dailyPower = dailyPowerService.calculateDailyPower(queryResult, localMidnightDto);
                dailyPowerService.insertMysql(localMidnightDto.getYesterday(), dailyPower, channelId, 0D);
            }
        }
    }

    @Scheduled(cron = "${schedule.cron2}")
    public void updateOutlier() throws JsonProcessingException {
        String key = "hourly_outlier";
        LocalDateTimeDto localDateTimeDto = outlierService.localDateTime();

        if (cronFlag) {
            OutlierDto outlier = outlierService.getOutlier(key);
            Optional<MinMaxDto> minMaxDto = outlierService.matchTime(outlier, localDateTimeDto);

            if (minMaxDto.isEmpty()) {
                log.info("outlier not found {}", key);
            }
            if (minMaxDto.isPresent()) {
                outlierRepo.setMin(minMaxDto.get().getMin());
                outlierRepo.setMax(minMaxDto.get().getMax());

                log.info("outlier update to min {} max {}", outlierRepo.getMin(), outlierRepo.getMax());
            }
        }
    }

    public void createMidnight() {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime todayMidNight = date.with(LocalTime.MIN);
        LocalDateTime yesterday = date.minusDays(1);
        LocalDateTime yesterdayMidnight = yesterday.with(LocalTime.MIN);

        log.info("Today Midnight {} ", todayMidNight);
        log.info("Yesterday Midnight {} ", yesterdayMidnight);

        localMidnightDto.setToday(todayMidNight);
        localMidnightDto.setYesterday(yesterdayMidnight);
    }

}
