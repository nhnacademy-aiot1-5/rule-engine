package live.ioteatime.ruleengine.service;

import java.util.Map;

public interface MappingTableService {
    /**
     * modbus 데이터를 파싱 하기 위한 매핑 테이블
     *  ChannelsRepository 애서 db에 저장된 매핑 테이블을 가져와 매핑테이블 저장소에 저장
     */
    void getMappingTable();

    /**
     * modbus address 과 매핑 테이블이 매칭되는 태그들을 가져오는 메소드
     * ex) 8000={description=sum, place=class_a, type=main}
     * @param address modbus address
     * @return Map<String, String>
     */
    Map<String, String> getTags(int address);
}

