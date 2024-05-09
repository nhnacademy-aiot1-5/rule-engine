package live.ioteatime.ruleengine.service.impl;

import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import live.ioteatime.ruleengine.repository.InfluxQueryRepository;
import live.ioteatime.ruleengine.domain.LocalMidnightDto;
import live.ioteatime.ruleengine.entity.DailyPowerEntity;
import live.ioteatime.ruleengine.repository.DailyElectricityConsumptionRepository;
import live.ioteatime.ruleengine.service.DailyPowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final InfluxQueryRepository influxQueryRepository;

    @Override
    public Map<LocalDateTime, Object> convertInfluxDbResponseToLocalTime(String dailyQuery) throws IllegalArgumentException {
        Map<LocalDateTime, Object> hourlyPowerDataMap = new HashMap<>();
        List<FluxTable> tables = influxQueryRepository.query(dailyQuery);

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

    @Override
    public double calculateDailyPower(Map<LocalDateTime, Object> hourlyPowerDataMap, LocalMidnightDto midNights) throws NullPointerException {
        Double today = (Double) hourlyPowerDataMap.get(midNights.getToday());
        Double yesterday = (Double) hourlyPowerDataMap.get(midNights.getYesterday());

        return today - yesterday;
    }

    @Override
    public void insertMysql(LocalDateTime yesterday, Double totalPower,int channelId,Double bill) {
        DailyPowerEntity save = dailyElectricityConsumptionRepository.save(createDailyEntity(yesterday,channelId,totalPower,bill));
        log.info("insert success date {} | channel {} | value {} | bill {}", save.getTime(),save.getChannelId() ,save.getKwh(),save.getBill());
    }

    @Override
    public int getQueryCount() {

        return influxQueryRepository.getQuerySize();
    }

    @Override
    public String getQuery(int index) {

        return influxQueryRepository.getQuery(index);
    }

    private DailyPowerEntity createDailyEntity(LocalDateTime midNights,int channelId ,Double totalPower,Double bill) {

        return DailyPowerEntity.builder().time(midNights).channelId(channelId).kwh(totalPower).bill(bill).build();
    }

}
