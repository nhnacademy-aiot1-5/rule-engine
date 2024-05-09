package live.ioteatime.ruleengine.service.impl;

import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import live.ioteatime.ruleengine.domain.InfluxQuery;
import live.ioteatime.ruleengine.domain.LocalMidnightDto;
import live.ioteatime.ruleengine.entity.DailyPowerEntity;
import live.ioteatime.ruleengine.repository.DailyElectricityConsumptionRepository;
import live.ioteatime.ruleengine.service.DailyPowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyPowerServiceImpl implements DailyPowerService {
    private final DailyElectricityConsumptionRepository dailyElectricityConsumptionRepository;
    private final QueryApi queryApi;
    private final InfluxQuery influxQuery;

    public int getQueryCount() {

        return influxQuery.getQueries().size();
    }

    public String getInfluxQuery(int index) {

        return influxQuery.getQueries().get(index);
    }

    public Map<LocalDateTime, Object> convertInfluxDbResponseToLocalTime(String dailyQuery) throws IllegalArgumentException {
        Map<LocalDateTime, Object> hourlyPowerDataMap = new HashMap<>();
        List<FluxTable> tables = queryApi.query(dailyQuery);

        if (tables.isEmpty()) {
            throw new IllegalArgumentException("please check your query");
        }

        for (FluxTable table : tables) {
            List<FluxRecord> records = table.getRecords();

            for (FluxRecord fluxRecord : records) {
                LocalDateTime localtime = LocalDateTime.ofInstant(Objects.requireNonNull(fluxRecord.getTime()), ZoneId.systemDefault());
                hourlyPowerDataMap.put(localtime, fluxRecord.getValue());
            }
        }

        return hourlyPowerDataMap;
    }

    public double calculateDailyPower(Map<LocalDateTime, Object> hourlyPowerDataMap, LocalMidnightDto midNights) throws NullPointerException {
        double today = (double) hourlyPowerDataMap.get(midNights.getToday());
        double yesterday = (double) hourlyPowerDataMap.get(midNights.getYesterday());

        return today - yesterday;
    }

    public void insertMysql(LocalDateTime yesterday, double totalPower) {
        DailyPowerEntity save = dailyElectricityConsumptionRepository.save(createDailyEntity(yesterday, totalPower));
        log.info("insert success date {} | data {}", save.getDate(), save.getValue());
    }

    private DailyPowerEntity createDailyEntity(LocalDateTime midNights, Double totalPower) {

        return DailyPowerEntity.builder().date(midNights).value(totalPower).build();
    }

    public LocalMidnightDto createMidnight() {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime todayMidNight = date.with(LocalTime.MIN);
        LocalDateTime yesterday = date.minusDays(1);
        LocalDateTime yesterdayMidnight = yesterday.with(LocalTime.MIN);

        log.info("Today Midnight {} ", todayMidNight);
        log.info("Yesterday Midnight {} ", yesterdayMidnight);

        return new LocalMidnightDto(yesterdayMidnight, todayMidNight);
    }

}
