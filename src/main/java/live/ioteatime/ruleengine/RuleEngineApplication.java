package live.ioteatime.ruleengine;

import live.ioteatime.ruleengine.properties.InfluxDBProperties;
import live.ioteatime.ruleengine.properties.RabbitmqInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({InfluxDBProperties.class, RabbitmqInfo.class})
public class RuleEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuleEngineApplication.class, args);
    }
}
