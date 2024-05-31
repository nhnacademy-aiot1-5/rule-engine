package live.ioteatime.ruleengine.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class OutlierDto {

    private String place;

    private List<MinMaxDto> values;
}
