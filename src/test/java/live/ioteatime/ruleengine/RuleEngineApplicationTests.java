package live.ioteatime.ruleengine;

import com.influxdb.client.InfluxDBClient;
import com.rabbitmq.client.Connection;
import live.ioteatime.ruleengine.manager.JSchManager;
import live.ioteatime.ruleengine.rule.RuleChain;
import live.ioteatime.ruleengine.scheduler.ReportScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
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
    ReportScheduler reportScheduler;

    @Test
    void contextLoads() {
    }

}
