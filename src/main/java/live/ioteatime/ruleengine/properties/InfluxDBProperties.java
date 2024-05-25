package live.ioteatime.ruleengine.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "influx")
@Getter
@Setter
public class InfluxDBProperties {
    private String url;
    private String org;
    private String main;
    private String outlier;
    private String token;
}
