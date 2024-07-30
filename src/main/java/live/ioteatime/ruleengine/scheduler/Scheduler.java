package live.ioteatime.ruleengine.scheduler;

import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.OutlierDto;
import live.ioteatime.ruleengine.handler.MqttDataHandlerContext;
import live.ioteatime.ruleengine.properties.InfluxDBProperties;
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

    private static final String LOGGING_INFLUX_INSERT = "insert data {}";

    @Value("${schedule.flag}")
    private boolean cronFlag;

    @Value("${outlier.redis.key}")
    private String outlierRedisKey;

    private final MqttDataHandlerContext mqttDataHandlerContext;
    private final OutlierService outlierService;
    private final InfluxDBProperties influxDBProperties;
    private final WriteApiBlocking writeApiBlocking;
    private final List<Point> points;
    int preSize;

    /**
     * 빈 생성시에 이상치 갱신
     */
    @PostConstruct
    private void firstStart() {
        outlierUpdate();
        preSize = points.size();
    }

    /**
     * 3초마다 points(데이터 리스트) 를 확인하여 들어온 데이터들 추가 데이터 있는지 확인후 한번에 insert
     */
    @Scheduled(fixedRate = 3000)
    public void bulkInsert() {
        synchronized (points) {
            if (!points.isEmpty()) {
                if ((preSize == points.size()) || points.size() >= 1000) {
                    writeApiBlocking.writePoints(influxDBProperties.getBucket(), influxDBProperties.getOrg(), points);
                    log.info(LOGGING_INFLUX_INSERT, points.size());
                    points.clear();
                }
            }
            preSize = points.size();
        }
    }

    /**
     * 이상치 갱신 전 모든 워커 스레드 종료, 다음 이상치 갱신
     * 레디스에 저장된 이상치를 가져와 이상치 저장소에 저장
     * 24시간제 시간, 현재 시간을 기준으로 시간만 잘라 이상치 저장소의 key 로 사용
     */
    @Scheduled(cron = "${schedule.cron}")
    public void outlierUpdater() {
        outlierUpdate();
    }

    private void outlierUpdate() {
        LocalDateTimeDto localDateTimeDto = TimeUtils.localDateTime();

        mqttDataHandlerContext.pauseAll();
        if (cronFlag) {
            try {
                String key = outlierRedisKey;
                List<OutlierDto> outlier = outlierService.getOutlier(key);
                outlierService.matchTime(outlier, localDateTimeDto);
            } catch (Exception e) {
                log.error("redis error : {}", e.getMessage());
            }
        }
        mqttDataHandlerContext.restartAll();
    }
}
