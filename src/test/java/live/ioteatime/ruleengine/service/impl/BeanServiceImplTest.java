package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.BeanSet;
import live.ioteatime.ruleengine.service.BeanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BeanServiceImplTest {

    @Autowired
    private BeanService beanService;
    @Test
    void createConfig(@TempDir File tempDir) throws Exception {
        BeanSet beanSet = new BeanSet();
        beanSet.setMqttId("testId");
        beanSet.setMqttHost("tcp://localhost:1883");
        beanSet.setMqttTopic("test/#");
        beanSet.setRabbitmqHost("test");
        beanSet.setRabbitmqPort("5672");
        beanSet.setRabbitmqUsername("test");
        beanSet.setRabbitmqPassword("test");
        beanSet.setRabbitmqExchange("restTopic");
        beanSet.setRabbitmqRoutingKey("data/#");

       String act= beanService.createConfig(beanSet, tempDir.getAbsolutePath() + File.separator);
        File file = new File(act);

        assertNotNull(file);
        assertTrue(file.exists());
    }
}