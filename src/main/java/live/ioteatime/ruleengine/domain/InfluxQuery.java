package live.ioteatime.ruleengine.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class InfluxQuery {
    private List<String> queries = new ArrayList<>();
}
