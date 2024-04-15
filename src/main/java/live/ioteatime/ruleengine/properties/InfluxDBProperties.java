package live.ioteatime.ruleengine.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "influx")
@Getter
@Setter(AccessLevel.PRIVATE)
public class InfluxDBProperties {

    private String url;
    private String org;
    private String bucket;
    private String token;
}
