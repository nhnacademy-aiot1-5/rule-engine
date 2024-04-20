package live.ioteatime.ruleengine.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class InfluxDBProperties {
    @Value("${influx.url}")
    private String url;
    @Value("${influx.org}")
    private String org;
    @Value("${influx.bucket}")
    private String bucket;
    @Value("${influx.token}")
    private String token;
}
