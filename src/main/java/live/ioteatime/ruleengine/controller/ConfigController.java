package live.ioteatime.ruleengine.controller;

import live.ioteatime.ruleengine.domain.ModbusInfo;
import live.ioteatime.ruleengine.domain.MqttInfo;
import live.ioteatime.ruleengine.exception.CreateJSchSessionException;
import live.ioteatime.ruleengine.service.CreateProperties;
import live.ioteatime.ruleengine.service.JschService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfigController {
    private final CreateProperties createProperties;
    private final JschService jschService;

    @PostMapping("/mqtt")
    public ResponseEntity<String> addBroker(@RequestBody MqttInfo mqttInfo) throws CreateJSchSessionException {
        String filePath = createProperties.createConfig(mqttInfo);
        log.info("addBroker file:{}", filePath);

        jschService.scpFile(filePath, mqttInfo.getMqttId(),"mqtt");

        return ResponseEntity.ok("create mqtt Bridge properties ");
    }

    @GetMapping("/delete/{type}/{bridgeName}")
    public ResponseEntity<String> deleteBridge(@PathVariable(name = "type")String type,@PathVariable String bridgeName) throws CreateJSchSessionException {
        jschService.deleteBridge(type,bridgeName);

        return ResponseEntity.ok("Delete Bridge ");
    }

    @PostMapping("/modbus")
    public ResponseEntity<String> addModbus(@RequestBody ModbusInfo modbusInfo) throws CreateJSchSessionException {
        String filePath = createProperties.createConfig(modbusInfo);
        log.info("addModbus file:{}", filePath);

        jschService.scpFile(filePath,modbusInfo.getName(),"modbus");

        return ResponseEntity.ok("create modbus Bridge properties ");
    }

}
