package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.OutlierDto;

import java.util.List;

public interface OutlierService {

    /**
     * 레디스에 저장된 이상치 를 가져와 OutlierDto 에 매핑하는 메소드
     *
     * @return Map<String, Map < Integer, MinMaxDto>>  매핑된 이상치
     */
    List<OutlierDto> getOutlier(String keys);

    /**
     * 현재 날짜, 시간에 맞는 이상치를 가져온다
     *
     * @param outlier          이상치
     * @param localDateTimeDto 현재 날짜, 시간
     */
    void matchTime(List<OutlierDto> outlier, LocalDateTimeDto localDateTimeDto);

    /**
     * 장소에 따른 이상치가 있는지 확인 하는 메소드
     * @param topicPlace 확인할 장소
     * @return 있으면 true
     */
    boolean checkOutlier(String topicPlace);

    /**
     * 장소에 맞는 이상치를 가져오는 메소드
     * @param topicPlace 확인할 장소
     * @return 장소에 따른 이상치
     */
    MinMaxDto getMinMax(String topicPlace);
}
