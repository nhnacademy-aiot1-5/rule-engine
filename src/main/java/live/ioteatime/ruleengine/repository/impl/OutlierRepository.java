package live.ioteatime.ruleengine.repository.impl;

import live.ioteatime.ruleengine.domain.MinMaxDto;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class OutlierRepository {

    private final Map<String, MinMaxDto> outliers = new HashMap<>();

    public void clearOutlier() {
        outliers.clear();
    }

    public List<String> getKeys() {
        return new ArrayList<>(outliers.keySet());
    }
}
