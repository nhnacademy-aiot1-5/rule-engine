package live.ioteatime.ruleengine.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutlierDto {
    private Map<Integer, MinMaxDto> hour;
}
