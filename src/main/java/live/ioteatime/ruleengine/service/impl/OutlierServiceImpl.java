package live.ioteatime.ruleengine.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.exception.MissingFieldException;
import live.ioteatime.ruleengine.repository.impl.OutlierRepository;
import live.ioteatime.ruleengine.service.OutlierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutlierServiceImpl implements OutlierService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final OutlierRepository outlierRepository;

    @Override
    public Map<String, Map<Integer, MinMaxDto>> getOutlier(List<String> keys) {

        Map<String, Map<Integer, MinMaxDto>> outlierMap = getOutlierMap(keys);
        log.info("testMap {}", outlierMap.entrySet());

        return outlierMap;
    }

    private Map<String, Map<Integer, MinMaxDto>> getOutlierMap(List<String> key) {
        Map<String, Map<Integer, MinMaxDto>> stringMapMap = new LinkedHashMap<>();

        for (String string : key) {
            String s = Optional.ofNullable(redisTemplate.opsForValue().get(string)).map(Object::toString)
                    .orElse(null);

            try {
                Map<String, Map<Integer, MinMaxDto>> stringMapMap1 = objectMapper.readValue(s,
                        new TypeReference<>() {
                        });

                stringMapMap.putAll(stringMapMap1);
            } catch (JsonProcessingException e) {
                throw new MissingFieldException(e.getMessage());
            }
        }

        return stringMapMap;
    }

    public List<String> getKeys(Map<String, Map<Integer, MinMaxDto>> outlierMap) {
        Set<String> keySet = outlierMap.keySet();

        return new ArrayList<>(keySet);
    }

    @Override
    public LocalDateTimeDto localDateTime() {
        int time = Integer.parseInt(LocalTime.now().toString().split(":")[0]);
        LocalDate localDate = LocalDate.now();

        log.info("time: {}", time);
        log.info("localDate: {}", localDate);

        return new LocalDateTimeDto(localDate, time);
    }

    @Override
    public Map<String, MinMaxDto> matchTime(Map<String, Map<Integer, MinMaxDto>> outlier, LocalDateTimeDto localDateTimeDto) {
        Map<String, MinMaxDto> outlierMap = new HashMap<>();
        List<String> keys = getKeys(outlier);

        for (int i = 0; i < outlier.size(); i++) {
            String key = keys.get(i);

            outlier.get(keys.get(i)).forEach((integer, minMaxDto) -> {
                if (integer.equals(localDateTimeDto.getTime()) && minMaxDto.getUpdatedAt().equals(localDateTimeDto.getDate())) {
                    outlierMap.put(key, minMaxDto);
                }
            });
        }
        log.info("matchTime : {}", outlierMap.entrySet());

        return outlierMap;
    }

    @Override
    public void updateOutlier(Map<String, MinMaxDto> outlier) {
        outlierRepository.clearOutlier();
        Map<String, MinMaxDto> outliers = outlierRepository.getOutliers();
        outliers.putAll(outlier);

        log.info("outlier update {}", outliers.entrySet());
    }

}
