package live.ioteatime.ruleengine.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReportScheduler {
    @Value("${schedule.flag}")
    private boolean cronFlag;

    @Scheduled(cron = "${schedule.cron1}")
    public void logTest1() {
        if (cronFlag){
        log.info("LogTest1 Time {}", System.currentTimeMillis());
        }
    }

    @Scheduled(cron = "${schedule.cron2}")
    public void logTest2() {
        if (cronFlag){
            log.info("LogTest2 Time {}", System.currentTimeMillis());
        }
    }

}
