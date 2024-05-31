package live.ioteatime.ruleengine.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.OutlierDto;
import live.ioteatime.ruleengine.exception.MissingFieldException;
import live.ioteatime.ruleengine.repository.impl.OutlierRepository;
import live.ioteatime.ruleengine.service.OutlierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutlierServiceImpl implements OutlierService {

    private static final String LOGGING_NOT_FOUND_REDIS_KEY = "redis is empty for key {}";
    private static final String LOGGING_GET_REDIS = "outlierList {}";
    private static final String LOGGING_GET_OUTLIER = "outliers {}";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final OutlierRepository outlierRepository;

    @Override
    public List<OutlierDto> getOutlier(String key) {
        List<OutlierDto> outlierList = new ArrayList<>();

        try {
            String outliers = Optional.ofNullable(redisTemplate.opsForValue().get(key)).map(Object::toString)
                    .orElse(null);

            if (outliers == null) {
                log.warn(LOGGING_NOT_FOUND_REDIS_KEY, key);

                return outlierList;
            }

            outlierList = objectMapper.readValue(outliers, objectMapper.getTypeFactory().constructCollectionType(List.class, OutlierDto.class));
            log.info(LOGGING_GET_REDIS, outlierList);
        } catch (JsonProcessingException e) {
            throw new MissingFieldException(e.getMessage());
        }
        return outlierList;
    }

    @Override
    public void matchTime(List<OutlierDto> outlier, LocalDateTimeDto localDateTimeDto) {
        outlierRepository.clearOutlier();

        for (OutlierDto outlierDto : outlier) {
            outlierDto.getValues().forEach(minMaxDto -> {
                if ((localDateTimeDto.getTime() == minMaxDto.getId()) && localDateTimeDto.getDate().equals(minMaxDto.getUpdatedAt())) {
                    outlierRepository.getOutliers().put(outlierDto.getPlace(), minMaxDto);
                }
            });
        }
        log.info(LOGGING_GET_OUTLIER, outlierRepository.getOutliers().entrySet());
    }

    @Override
    public boolean checkOutlier(String topicPlace) {
        return outlierRepository.getKeys().contains(topicPlace);
    }

    @Override
    public MinMaxDto getMinMax(String topicPlace) {
        return outlierRepository.getOutliers().get(topicPlace);
    }
}
