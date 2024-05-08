package live.ioteatime.ruleengine.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QueryResponse {
    private int number;
    private String query;
}
