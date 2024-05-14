package live.ioteatime.ruleengine.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import live.ioteatime.ruleengine.domain.*;
import live.ioteatime.ruleengine.service.OutlierService;
import live.ioteatime.ruleengine.service.impl.DailyPowerServiceImpl;
import live.ioteatime.ruleengine.service.impl.QueryServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
    @Value("${schedule.flag}")
    private boolean cronFlag;
    LocalMidnightDto localMidnightDto = new LocalMidnightDto();
    private static final String KEY = "hourly_outlier";

    @EventListener(ApplicationReadyEvent.class)
    public void firstStart() throws JsonProcessingException {
        outlierUpdate();
    }

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
    public void outlierUpdater() throws JsonProcessingException {
        outlierUpdate();
    }

    private void outlierUpdate() throws JsonProcessingException {
        LocalDateTimeDto localDateTimeDto = outlierService.localDateTime();

        if (cronFlag) {
            OutlierDto outlier = outlierService.getOutlier(ReportScheduler.KEY);
            Optional<MinMaxDto> minMaxDto = outlierService.matchTime(outlier, localDateTimeDto);

            if (minMaxDto.isEmpty()) {
                log.info("outlier not found {}", ReportScheduler.KEY);
            }

            minMaxDto.ifPresent(outlierService::updateOutlier);
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
