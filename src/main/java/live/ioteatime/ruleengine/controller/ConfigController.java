package live.ioteatime.ruleengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import live.ioteatime.ruleengine.domain.ModbusInfo;
import live.ioteatime.ruleengine.domain.MqttInfo;
import live.ioteatime.ruleengine.exception.CreateJSchSessionException;
import live.ioteatime.ruleengine.service.CreateProperties;
import live.ioteatime.ruleengine.service.JschService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "bridge", description = "브릿지를 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfigController {
    private final CreateProperties createProperties;
    private final JschService jschService;

    /**
     * mqttBridge 의 설정들을 받아 properties 를 생성해 bridge 서버에 전송하고 어플리케이션 실행
     *
     * @param mqttInfo 브릿지를 만들기위한 설정 파일
     * @return create mqtt Bridge properties
     * @throws CreateJSchSessionException 브릿지 서버랑 연결 실패할 경우
     */
    @Operation(summary = "MQTT 브릿지 추가",
            description = "MQTT 브로커의 데이터를 수집 하기 위한 브릿지를 생성 합니다.")
    @ApiResponse(responseCode = "200", description = "create mqtt Bridge properties")
    @PostMapping("/mqtt")
    public ResponseEntity<String> mqttBridge(@RequestBody MqttInfo mqttInfo) throws CreateJSchSessionException {
        String filePath = createProperties.createConfig(mqttInfo);
        log.info("addBroker file:{}", filePath);

        jschService.scpFile(filePath, mqttInfo.getMqttId(), "mqtt");

        return ResponseEntity.ok("create mqtt Bridge properties");
    }

    /**
     * 브릿지를 제거하는 컨트롤러
     *
     * @param type       mqtt,modbus
     * @param bridgeName 브릿지 이름
     * @return Delete Bridge
     * @throws CreateJSchSessionException 브릿지 서버랑 연결 실패할 경우
     */
    @Operation(summary = "브릿지 제거",
            description = "type(MQTT,MODBUS) 에 따른 브릿지를 제거 합니다.")
    @ApiResponse(responseCode = "200", description = "Delete Bridge")
    @GetMapping("/delete/{type}/{bridgeName}")
    public ResponseEntity<String> deleteBridge(@Parameter(description = "제거할 브릿지의 타입 (MQTT, MODBUS)")
                                               @PathVariable(name = "type") String type
            , @Parameter(description = "제거할 브릿지의 이름") @PathVariable String bridgeName)
            throws CreateJSchSessionException {
        jschService.deleteBridge(type, bridgeName);

        return ResponseEntity.ok("Delete Bridge");
    }

    /**
     * modbus 브릿지의 설정을 받아 properties 를 생성해 bridge 서버에 전송하고 어플리케이션 실행
     *
     * @param modbusInfo modbus 정보
     * @return create modbus Bridge properties
     * @throws CreateJSchSessionException 브릿지 서버랑 연결 실패할 경우
     */
    @Operation(summary = "MODBUS 브릿지 추가",
            description = "MODBUS 브로커의 데이터를 수집 하기 위한 브릿지를 생성 합니다.")
    @ApiResponse(responseCode = "200", description = "create modbus Bridge properties", content = @Content(mediaType = "text/plain"))
    @PostMapping("/modbus")
    public ResponseEntity<String> modbusBridge(@RequestBody ModbusInfo modbusInfo) throws CreateJSchSessionException {
        String filePath = createProperties.createConfig(modbusInfo);
        log.info("addModbus file:{}", filePath);

        jschService.scpFile(filePath, modbusInfo.getName(), "modbus");

        return ResponseEntity.ok("create modbus Bridge properties");
    }

}
