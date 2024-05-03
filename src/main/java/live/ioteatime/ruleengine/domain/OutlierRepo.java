package live.ioteatime.ruleengine.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class OutlierRepo {
    private double min;
    private double max;
}
