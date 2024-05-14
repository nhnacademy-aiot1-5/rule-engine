package live.ioteatime.ruleengine.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.OutlierDto;
import live.ioteatime.ruleengine.repository.OutlierRepository;
import live.ioteatime.ruleengine.service.OutlierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutlierServiceImpl implements OutlierService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final OutlierRepository outlierRepository;

    @Override
    public OutlierDto getOutlier(String key) throws JsonProcessingException {
        String o = Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .map(Object::toString).orElse(null);

        return objectMapper.readValue(o, OutlierDto.class);
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
    public Optional<MinMaxDto> matchTime(OutlierDto outlierDto, LocalDateTimeDto localDateTimeDto) {
        Map<Integer, MinMaxDto> outliers = outlierDto.getHour();

        return outliers.entrySet().stream()
                .filter(h -> h.getKey() == localDateTimeDto.getTime())
                .filter(e -> e.getValue().getUpdatedAt().equals(localDateTimeDto.getDate()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public void updateOutlier(MinMaxDto minMaxDto) {
        outlierRepository.setMin(minMaxDto.getMin());
        outlierRepository.setMax(minMaxDto.getMax());

        log.info("outlier update to min {} max {}", outlierRepository.getMin(), outlierRepository.getMax());
    }

}
