package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.domain.QueryRequest;
import live.ioteatime.ruleengine.domain.QueryResponse;
import live.ioteatime.ruleengine.service.impl.QueryServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InfluxQueryController {
    private final QueryServiceImpl queryServiceImpl;

    @PostMapping("/query")
    public ResponseEntity<String> addQuery(@RequestBody QueryRequest query) {
        queryServiceImpl.addQuery(query);

        return ResponseEntity.ok("Create Query");
    }

    @GetMapping("/query")
    public ResponseEntity<List<QueryResponse>> getQuery() {

        return ResponseEntity.ok(queryServiceImpl.getQueries());
    }

    @GetMapping("/delete/query/{index}")
    public ResponseEntity<String> deleteQuery(@PathVariable int index) {
        String deleteQuery = queryServiceImpl.deleteQuery(index);

        return ResponseEntity.ok("delete " + deleteQuery);
    }

}
