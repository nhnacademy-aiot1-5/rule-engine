package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.domain.BeanSet;
import live.ioteatime.ruleengine.exception.CreateJSchSessionException;
import live.ioteatime.ruleengine.service.BeanService;
import live.ioteatime.ruleengine.service.JschService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final BeanService beanService;
    private final JschService jschService;
    @Value("${create.folder.path}")
    private String path;

    @PostMapping("/addBroker")
    public ResponseEntity<String> addBroker(@RequestBody BeanSet beanSet) throws CreateJSchSessionException {
       String filePath= beanService.createConfig(beanSet,path);
       log.info("addBroker file:{}",filePath);

        jschService.scpFile(filePath,beanSet.getMqttId());

        return ResponseEntity.ok("Create Properties ");
    }
}
