package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.service.MappingTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MappingTableController {
    private final MappingTableService mappingTableService;

    @GetMapping("/update/mapping-table")
    public ResponseEntity<String> getMappingTable() {
        mappingTableService.getMappingTable();

        return ResponseEntity.ok("update mapping-table");
    }

}
