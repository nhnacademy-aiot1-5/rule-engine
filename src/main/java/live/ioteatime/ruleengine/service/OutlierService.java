package live.ioteatime.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.OutlierDto;

import java.util.Optional;

public interface OutlierService {

    /**
     *  레디스에 저장된 이상치 를 가져와 OutlierDto 에 매핑하는 메소드
     *
     * @param key 레디스 키
     * @return OutlierDto  매핑된 이상치
     * @throws JsonProcessingException 파싱실패 시
     */
    OutlierDto getOutlier(String key) throws JsonProcessingException;

    /**
     *  현재날짜 현재 시간을 가져온다
     * @return LocalDateTimeDto 현재 날짜 시간 매핑
     */
    LocalDateTimeDto localDateTime();

    /**
     *  현재 날짜, 시간에 맞는 이상치를 가져온다
     * @param outlierDto 이상치
     * @param localDateTimeDto 현재 날짜, 시간
     * @return MinMaxDto 이상치 최소값, 최대값
     */
    Optional<MinMaxDto> matchTime(OutlierDto outlierDto, LocalDateTimeDto localDateTimeDto);

    /**
     *  이상치를 outlierRepository 에 저장한다
     * @param minMaxDto 이상치 최소값 최대 값
     */
    void updateOutlier(MinMaxDto minMaxDto);
}
