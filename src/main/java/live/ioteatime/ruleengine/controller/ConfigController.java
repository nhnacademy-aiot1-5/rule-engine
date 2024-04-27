package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.domain.MqttInfo;
import live.ioteatime.ruleengine.exception.CreateJSchSessionException;
import live.ioteatime.ruleengine.service.CreateProperties;
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
    private final CreateProperties createProperties;
    private final JschService jschService;
    @Value("${create.folder.path}")
    private String path;

    @PostMapping("/addBroker")
    public ResponseEntity<String> addBroker(@RequestBody MqttInfo mqttInfo) throws CreateJSchSessionException {
        String filePath = createProperties.createConfig(mqttInfo, path);
        log.info("addBroker file:{}", filePath);

        jschService.scpFile(filePath, mqttInfo.getMqttId());

        return ResponseEntity.ok("Create Properties ");
    }

}
