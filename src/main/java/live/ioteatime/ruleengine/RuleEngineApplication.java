package live.ioteatime.ruleengine;

import live.ioteatime.ruleengine.properties.InfluxDBProperties;
import live.ioteatime.ruleengine.properties.JschProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({InfluxDBProperties.class, JschProperties.class})
@EnableScheduling
public class RuleEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuleEngineApplication.class, args);
    }
}
