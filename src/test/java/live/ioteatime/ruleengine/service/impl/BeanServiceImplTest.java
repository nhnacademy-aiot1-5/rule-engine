package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.BeanSet;
import live.ioteatime.ruleengine.service.BeanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class BeanServiceImplTest {
    private BeanService beanService;

    @BeforeEach
    void setUp() {

        RabbitProperties rabbitPropertiesMock = mock(RabbitProperties.class);
        RabbitProperties.Template templateMock = mock(RabbitProperties.Template.class);

        when(rabbitPropertiesMock.getHost()).thenReturn("localhost");
        when(rabbitPropertiesMock.getPort()).thenReturn(5672);
        when(rabbitPropertiesMock.getUsername()).thenReturn("user");
        when(rabbitPropertiesMock.getPassword()).thenReturn("password");
        when(rabbitPropertiesMock.getTemplate()).thenReturn(templateMock);
        when(templateMock.getExchange()).thenReturn("testExchange");
        when(templateMock.getRoutingKey()).thenReturn("testRoutingKey");
        beanService =new BeanServiceImpl(rabbitPropertiesMock);

    }
    @Test
    void createConfig(@TempDir File tempDir){
        BeanSet beanSet = new BeanSet();
        beanSet.setMqttId("testId");
        beanSet.setMqttHost("tcp://localhost:1883");
        beanSet.setMqttTopic("test/#");

        String resultFilePath = beanService.createConfig(beanSet, tempDir.getAbsolutePath());
        String expectedFilePath = tempDir.getAbsolutePath() + "application-prod.properties";
        assertEquals(expectedFilePath, resultFilePath);


        File resultFile = new File(resultFilePath);
        assertEquals(true, resultFile.exists());

    }
}