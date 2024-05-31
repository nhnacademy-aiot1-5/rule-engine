package live.ioteatime.ruleengine.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "MODBUS 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModbusInfo {

    @Schema(description = "MODBUS NAME",example = "modbus name")
    private String name;

    @Schema(description = "MODBUS HOST",example = "local host")
    private String host;

    @Schema(description = "MODBUS CHANNELS",example = "\"function-code/address/type, function-code/address/type....\"")
    private String channel;
}

