package live.ioteatime.ruleengine.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import live.ioteatime.ruleengine.domain.*;
import live.ioteatime.ruleengine.service.OutlierService;
import live.ioteatime.ruleengine.service.impl.DailyPowerServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportScheduler {
    private final DailyPowerServiceImpl dailyPowerService;
    private final OutlierService outlierService;
    private final OutlierRepo outlierRepo;
    @Value("${schedule.flag}")
    private boolean cronFlag;

    @Scheduled(cron = "${schedule.cron1}")
    public void saveTodayPower() {
        if (cronFlag) {
            LocalMidnightDto localMidnightDto = dailyPowerService.createMidnight();

            for (int i = 0; i < dailyPowerService.getQueryCount(); i++) {
                Map<LocalDateTime, Object> queryResult = dailyPowerService.convertInfluxDbResponseToLocalTime(dailyPowerService.getInfluxQuery(i));
                double dailyPower = dailyPowerService.calculateDailyPower(queryResult, localMidnightDto);
                dailyPowerService.insertMysql(localMidnightDto.getYesterday(), dailyPower);
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

}
