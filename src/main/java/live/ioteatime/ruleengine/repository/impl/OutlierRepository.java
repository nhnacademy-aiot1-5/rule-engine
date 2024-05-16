package live.ioteatime.ruleengine.repository.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class OutlierRepository {
    private double min;
    private double max;
}
