package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.domain.QueryRequest;
import live.ioteatime.ruleengine.domain.QueryResponse;
import live.ioteatime.ruleengine.manager.QueryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InfluxQueryController {
    private final QueryManager queryManager;

    @PostMapping("/query")
    public ResponseEntity<String> addQuery(@RequestBody QueryRequest query) {
        queryManager.addQuery(query);

        return ResponseEntity.ok("Create Query");
    }

    @GetMapping("/query")
    public ResponseEntity<List<QueryResponse>> getQuery() {

        return ResponseEntity.ok(queryManager.getQueries());
    }

    @GetMapping("/delete/{index}")
    public ResponseEntity<String> deleteQuery(@PathVariable int index) {
        String deleteQuery = queryManager.deleteQuery(index);

        return ResponseEntity.ok("delete " + deleteQuery);
    }

}
