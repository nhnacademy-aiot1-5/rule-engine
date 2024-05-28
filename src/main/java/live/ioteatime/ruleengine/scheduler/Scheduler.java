package live.ioteatime.ruleengine.scheduler;

import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.OutlierDto;
import live.ioteatime.ruleengine.handler.MqttDataHandlerContext;
import live.ioteatime.ruleengine.service.OutlierService;
import live.ioteatime.ruleengine.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
    private final MqttDataHandlerContext mqttDataHandlerContext;
    private final OutlierService outlierService;
    @Value("${schedule.flag}")
    private boolean cronFlag;

    @EventListener(ApplicationReadyEvent.class)
    private void firstStart() {
        outlierUpdate();
    }

    @Scheduled(cron = "${schedule.cron2}")
    public void outlierUpdater() {
        outlierUpdate();
    }

    private void outlierUpdate() {
        LocalDateTimeDto localDateTimeDto = TimeUtils.localDateTime();

        mqttDataHandlerContext.pauseAll();
        if (cronFlag) {
            String key = "outliers";
            List<OutlierDto> outlier = outlierService.getOutlier(key);
            outlierService.matchTime(outlier, localDateTimeDto);
        }
        mqttDataHandlerContext.restartAll();
    }

}
