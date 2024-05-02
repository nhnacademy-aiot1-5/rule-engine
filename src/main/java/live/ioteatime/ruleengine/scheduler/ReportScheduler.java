package live.ioteatime.ruleengine.scheduler;

import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import live.ioteatime.ruleengine.domain.DailyPower;
import live.ioteatime.ruleengine.domain.InfluxQuery;
import live.ioteatime.ruleengine.domain.LocalMidnightDto;
import live.ioteatime.ruleengine.repository.DailyElectricityConsumptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportScheduler {
    private final DailyElectricityConsumptionRepository dailyElectricityConsumptionRepository;
    private final QueryApi queryApi;
    private final InfluxQuery influxQuery;
    @Value("${schedule.flag}")
    private boolean cronFlag;

    @Scheduled(cron = "${schedule.cron1}")
    public void saveTodayPower() {
        if (cronFlag) {
            try {

                for (int i = 0; i < influxQuery.getQueries().size(); i++) {
                    LocalMidnightDto localMidnightDto = createMidnight();
                    Map<LocalDateTime, Object> hourlyPowerDataMap = getQuery(influxQuery.getQueries().get(i), queryApi);
                    double totalPower = getTotalPower(hourlyPowerDataMap, localMidnightDto);
                    insertMysql(localMidnightDto, totalPower);
                    log.info("query info {}", influxQuery.getQueries().get(i));
                }

            } catch (IllegalArgumentException e) {
                log.debug("query is null {}", e.getMessage());
            } catch (NullPointerException e) {
                log.error("please check your query {}", e.getMessage());
            }
        }
    }

    private void insertMysql(LocalMidnightDto localMidnightDto, double totalPower) {
        DailyPower save = dailyElectricityConsumptionRepository.save(createDailyEntity(localMidnightDto.getYesterday(), totalPower));
        log.info("insert success date {} | data {}", save.getDate(), save.getValue());
    }

    /**
     * influxdb 에 하루치 전력 총 합을 가져와 로컬 시간으로 바꾸는 메소드
     *
     * @param dailyQuery 쿼리문
     * @param queryApi   influx 쿼리
     * @return Map<LocalDateTime, Object> 로컬 시간으로 바꾼 맵
     */
    private Map<LocalDateTime, Object> getQuery(String dailyQuery, QueryApi queryApi) throws IllegalArgumentException {
        Map<LocalDateTime, Object> hourlyPowerDataMap = new HashMap<>();
        List<FluxTable> tables = queryApi.query(dailyQuery);

        for (FluxTable table : tables) {
            List<FluxRecord> records = table.getRecords();

            for (FluxRecord fluxRecord : records) {
                LocalDateTime localtime = LocalDateTime.ofInstant(Objects.requireNonNull(fluxRecord.getTime()), ZoneId.systemDefault());
                hourlyPowerDataMap.put(localtime, fluxRecord.getValue());
            }

        }

        return hourlyPowerDataMap;
    }

    /**
     * 하루치 총 전력을 계산 하는 메소드
     * 총 전력량 에서 전날의 전력량을 빼기
     *
     * @param hourlyPowerDataMap 시간과 값이 들어있는 맵
     * @param midNights          전날과 그날의 00:00
     * @return double
     */
    private double getTotalPower(Map<LocalDateTime, Object> hourlyPowerDataMap, LocalMidnightDto midNights) throws NullPointerException {
        double today = (double) hourlyPowerDataMap.get(midNights.getToday());
        double yesterday = (double) hourlyPowerDataMap.get(midNights.getYesterday());

        return today - yesterday;
    }

    private DailyPower createDailyEntity(LocalDateTime midNights, Double totalPower) {

        return DailyPower.builder().date(midNights).value(totalPower).build();
    }

    /**
     * 당일의 00:00 부터 다음날 00:00
     *
     * @return List<LocalDateTime>
     */
    private LocalMidnightDto createMidnight() {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime todayMidNight = date.with(LocalTime.MIN);
        LocalDateTime yesterday = date.minusDays(1);
        LocalDateTime yesterdayMidnight = yesterday.with(LocalTime.MIN);

        log.info("Today Midnight {} ", todayMidNight);
        log.info("Yesterday Midnight {} ", yesterdayMidnight);

        return new LocalMidnightDto(yesterdayMidnight, todayMidNight);
    }

}
