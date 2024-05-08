package live.ioteatime.ruleengine.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModbusInfo {
    private String name;
    private String host;
    private int port;
    private String channel;
}

