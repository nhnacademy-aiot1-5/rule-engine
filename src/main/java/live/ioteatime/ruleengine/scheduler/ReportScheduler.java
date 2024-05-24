package live.ioteatime.ruleengine.scheduler;

import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.LocalMidnightDto;
import live.ioteatime.ruleengine.domain.OutlierDto;
import live.ioteatime.ruleengine.service.OutlierService;
import live.ioteatime.ruleengine.service.impl.DailyPowerServiceImpl;
import live.ioteatime.ruleengine.service.impl.QueryServiceImpl;
import live.ioteatime.ruleengine.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportScheduler {
    private final DailyPowerServiceImpl dailyPowerService;
    private final QueryServiceImpl queryServiceImpl;
    private final OutlierService outlierService;
    @Value("${schedule.flag}")
    private boolean cronFlag;

    @EventListener(ApplicationReadyEvent.class)
    public void firstStart() {
        outlierUpdate();
    }

    @Scheduled(cron = "${schedule.cron1}")
    public void saveTodayPower() {
        if (cronFlag) {
            LocalMidnightDto midnight = TimeUtils.createMidnight();

            for (int i = 0; i < dailyPowerService.getQueryCount(); i++) {
                Map<LocalDateTime, Object> queryResult = dailyPowerService.convertInfluxDbResponseToLocalTime(dailyPowerService.getQuery(i));
                int channelId = queryServiceImpl.getChannelId(i);
                double dailyPower = dailyPowerService.calculateDailyPower(queryResult, midnight);
                dailyPowerService.insertMysql(midnight.getYesterday(), dailyPower, channelId, 0D);
            }
        }
    }

    @Scheduled(cron = "${schedule.cron2}")
    public void outlierUpdater() {
        outlierUpdate();
    }

    private void outlierUpdate() {
        LocalDateTimeDto localDateTimeDto = TimeUtils.localDateTime();

        if (cronFlag) {
            String key = "outliers";
            List<OutlierDto> outlier = outlierService.getOutlier(key);
            outlierService.matchTime(outlier, localDateTimeDto);
        }
    }

}
