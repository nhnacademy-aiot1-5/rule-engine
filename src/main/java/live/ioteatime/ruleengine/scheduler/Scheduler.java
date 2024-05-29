package live.ioteatime.ruleengine.scheduler;

import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.OutlierDto;
import live.ioteatime.ruleengine.handler.MqttDataHandlerContext;
import live.ioteatime.ruleengine.service.OutlierService;
import live.ioteatime.ruleengine.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
    private final MqttDataHandlerContext mqttDataHandlerContext;
    private final OutlierService outlierService;
    @Value("${schedule.flag}")
    private boolean cronFlag;

    /**
     * 빈 생성시에 이상치 갱신
     */
    @PostConstruct
    private void firstStart() {
        outlierUpdate();
    }

    /**
     * 이상치 갱신 전 모든 워커 스레드 종료, 다음 이상치 갱신
     * 레디스에 저장된 이상치를 가져와 이상치 저장소에 저장
     * 24시간제 시간, 현재 시간을 기준으로 시간만 잘라 이상치 저장소의 key 로 사용
     */
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
