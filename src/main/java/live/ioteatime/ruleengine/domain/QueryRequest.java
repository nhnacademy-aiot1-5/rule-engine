package live.ioteatime.ruleengine.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class QueryRequest {
    private String bucket;
    private String range;
    private Map<String,String> filters;
    private String window;
    private String fn;
    private String yield;
}
