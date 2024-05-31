package live.ioteatime.ruleengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import live.ioteatime.ruleengine.handler.MqttDataHandlerContext;
import live.ioteatime.ruleengine.service.MappingTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Modbus Mapping", description = "모드버스 매핑을 위한 매핑테이블 업데이트 API")
@RestController
@RequiredArgsConstructor
public class MappingTableController {

    private final MappingTableService mappingTableService;
    private final MqttDataHandlerContext mqttDataHandlerContext;

    /**
     * database 에 저장된 매핑 테이블을 가져와 매핑테이블 갱신
     * @return update mapping-table
     */
    @Operation(summary = "MODBUS Mapping Table 갱신",
            description = "모드 버스 매핑을 위한 매필테이블 갱신 합니다.")
    @ApiResponse(responseCode = "200", description = "update mapping-table")
    @GetMapping("/update/mapping-table")
    public ResponseEntity<String> getMappingTable() {
        mqttDataHandlerContext.pauseAll();
        mappingTableService.getMappingTable();
        mqttDataHandlerContext.restartAll();

        return ResponseEntity.ok("update mapping-table");
    }
}
