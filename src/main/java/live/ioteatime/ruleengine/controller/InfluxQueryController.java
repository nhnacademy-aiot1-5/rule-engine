package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.domain.QueryRequest;
import live.ioteatime.ruleengine.manager.QueryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InfluxQueryController {
    private final QueryManager queryManager;

    @PostMapping("/query")
    public ResponseEntity<String> setQuery(@RequestBody QueryRequest query) {
        queryManager.setUp(query);

        return ResponseEntity.ok("Create Query");
    }

}
