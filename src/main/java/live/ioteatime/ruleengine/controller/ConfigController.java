package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.domain.BeanSet;
import live.ioteatime.ruleengine.service.BeanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final BeanService beanService;
    private String path= "src/main/resources/";
    @PostMapping("/addBroker")
    public ResponseEntity<String> addBroker(@RequestBody BeanSet beanSet) {
       String filePath= beanService.createConfig(beanSet,path);
       log.info("addBroker file:{}",filePath);
        return ResponseEntity.ok("Create Properties ");
    }
}
