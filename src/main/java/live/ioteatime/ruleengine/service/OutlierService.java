package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.MinMaxDto;

import java.util.List;
import java.util.Map;

public interface OutlierService {

    /**
     * 레디스에 저장된 이상치 를 가져와 OutlierDto 에 매핑하는 메소드
     *
     * @return Map<String, Map < Integer, MinMaxDto>>  매핑된 이상치
     */
    Map<String, Map<Integer, MinMaxDto>> getOutlier(List<String> keys);

    /**
     * 현재날짜 현재 시간을 가져온다
     *
     * @return LocalDateTimeDto 현재 날짜 시간 매핑
     */
    LocalDateTimeDto localDateTime();

    /**
     * 현재 날짜, 시간에 맞는 이상치를 가져온다
     *
     * @param outlier          이상치
     * @param localDateTimeDto 현재 날짜, 시간
     * @return MinMaxDto 이상치 최소값, 최대값
     */
    Map<String, MinMaxDto> matchTime(Map<String, Map<Integer, MinMaxDto>> outlier, LocalDateTimeDto localDateTimeDto);

    /**
     * 이상치를 outlierRepository 에 저장한다
     *
     * @param outlier 이상치 최소값 최대 값
     */
    void updateOutlier(Map<String, MinMaxDto> outlier);
}
