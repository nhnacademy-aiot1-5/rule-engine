package live.ioteatime.ruleengine;

import com.influxdb.client.InfluxDBClient;
import com.rabbitmq.client.Connection;
import live.ioteatime.ruleengine.manager.JSchManager;
import live.ioteatime.ruleengine.properties.InfluxDBProperties;
import live.ioteatime.ruleengine.properties.JschProperties;
import live.ioteatime.ruleengine.repository.ChannelsRepository;
import live.ioteatime.ruleengine.rule.RuleChain;
import live.ioteatime.ruleengine.scheduler.Scheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = RuleEngineApplication.class)
@EnableConfigurationProperties({InfluxDBProperties.class, JschProperties.class})
class RuleEngineApplicationTests {
    @MockBean
    RuleChain ruleChain;
    @MockBean
    InfluxDBClient influxDBClient;
    @MockBean
    Connection connection;
    @MockBean
    JSchManager jSchManager;
    @MockBean
    Scheduler scheduler;
    @MockBean
    ChannelsRepository channelsRepository;

    @Autowired
    InfluxDBProperties influxDBProperties;
    @Autowired
    JschProperties jschProperties;

    @Test
    void contextLoads() {
    }

    @Test
    void PropertiesLoadTest() {
        assertThat(influxDBProperties).isNotNull();
        assertThat(jschProperties).isNotNull();
    }
}
