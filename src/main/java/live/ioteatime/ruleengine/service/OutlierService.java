package live.ioteatime.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.OutlierDto;

import java.util.Optional;

public interface OutlierService {

    OutlierDto getOutlier(String key) throws JsonProcessingException;

    LocalDateTimeDto localDateTime();

    Optional<MinMaxDto> matchTime(OutlierDto outlierDto, LocalDateTimeDto localDateTimeDto);
}
