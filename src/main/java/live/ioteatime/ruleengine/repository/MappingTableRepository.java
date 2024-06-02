package live.ioteatime.ruleengine.repository;

import live.ioteatime.ruleengine.domain.MappingData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MappingTableRepository {

    /**
     *  모든 매핑테이블 조회하는 메소드
     *  ex) [8000={description=sum, place=class_a, type=main}, 8032={description=sum, place=class_a, type=outdoor_unit_room_light}]
     * @return Set<Map.Entry<Integer, Map<String, String>>> 만들어진 맵 entry
     */
    Set<Map.Entry<Integer, Map<String, String>>> getTables();

    /**
     *  매핑테이블에서 원하는 태그 가져오는 메소드
     *  ex) 8000 = {description=sum, place=class_a, type=main}
     * @param address 모드버스 address
     * @return Map<String, String> address 에 따른 매핑테이블
     */
    Map<String, String> getTags(int address);

    /**
     *  매핑테이블 매핑 추가하는 메소드
     * @param table 추가할 테이블
     */
    void addValue(List<MappingData> table);
}
