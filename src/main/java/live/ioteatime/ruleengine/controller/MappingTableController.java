package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.handler.MqttDataHandlerContext;
import live.ioteatime.ruleengine.service.MappingTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MappingTableController {
    private final MappingTableService mappingTableService;
    private final MqttDataHandlerContext mqttDataHandlerContext;

    /**
     * database 에 저장된 매핑 테이블을 가져와 매핑테이블 갱신
     * @return update mapping-table
     */
    @GetMapping("/update/mapping-table")
    public ResponseEntity<String> getMappingTable() {
        mqttDataHandlerContext.pauseAll();
        mappingTableService.getMappingTable();
        mqttDataHandlerContext.restartAll();
        return ResponseEntity.ok("update mapping-table");
    }

}
