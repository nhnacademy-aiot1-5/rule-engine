package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.ChannelDto;
import live.ioteatime.ruleengine.domain.PlaceDto;

import java.util.List;

public interface MappingTableService {

    /**
     *  db의 channel 테이블의 데이터를 가져와 dto 로 전환 하는 메소드
     * @return List<ChannelDto> dto 로 전환한 리스트
     */
    List<ChannelDto> getChannels();

    /**
     *   db의 place 테이블의 데이터를 가져와 dto 로 전환 하는 메소드
     * @return List<PlaceDto> dto 로 전환한 리스트
     */
    List<PlaceDto> getPlaces();

    /**
     *  type 8000 = main, 8016 = automatic_door
     *  phase quantity = address +  0 ~5 = kwh, address + 6~11 = kvarh , address + 12~17 kvah
     *  description address + quantity = 0~1 = sum, 2~3 = this month 4 ~ 6 = last month
     * @param channels db 에서 가져온 channel 리스트
     * @param places db 에서 가져온 place 리스트
     */
     void setTable(List<ChannelDto> channels, List<PlaceDto> places);
}
