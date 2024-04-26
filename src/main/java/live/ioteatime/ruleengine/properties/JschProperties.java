package live.ioteatime.ruleengine.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ssh")
@Setter
@Getter
public class JschProperties {

    private String privateKey;
    private String user;
    private String host;
    private String savePath;
    private String folderPath;

}
