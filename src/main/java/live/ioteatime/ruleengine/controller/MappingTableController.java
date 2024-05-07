package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.domain.ChannelDto;
import live.ioteatime.ruleengine.domain.PlaceDto;
import live.ioteatime.ruleengine.mapping_table.MappingTable;
import live.ioteatime.ruleengine.service.MappingTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MappingTableController {
    private final MappingTableService mappingTableService;
    private final MappingTable mappingTable;

    /**
     * 매핑 테이블을 갱신 하기 위한 클래스 입니다.
     */
    @GetMapping("update/mapping-table")
    public void getMappingTable() {
        List<ChannelDto> channelDtos = mappingTableService.getChannels();
        List<PlaceDto> places = mappingTableService.getPlaces();

        mappingTableService.setTable(channelDtos, places);

        log.info("place {}", mappingTable.getPlace().entrySet());
        log.info("getPhase {}", mappingTable.getPhase().entrySet());
        log.info("getDescription {}", mappingTable.getDescription().entrySet());
        log.info("getTypes {}", mappingTable.getTypes().entrySet());
    }

}
