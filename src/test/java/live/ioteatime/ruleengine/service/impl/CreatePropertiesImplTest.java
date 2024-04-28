package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.MqttInfo;
import live.ioteatime.ruleengine.service.CreateProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CreatePropertiesImplTest {
    private CreateProperties createProperties;

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

        createProperties =new CreatePropertiesImpl(rabbitPropertiesMock);
    }

    @Test
    void createConfig(@TempDir File tempDir){
        MqttInfo mqttInfo = new MqttInfo();
        ReflectionTestUtils.setField(mqttInfo, "mqttHost", "localhost");
        ReflectionTestUtils.setField(mqttInfo, "mqttId", "test");
        ReflectionTestUtils.setField(mqttInfo, "mqttTopic", "test/data");

        String resultFilePath = createProperties.createConfig(mqttInfo, tempDir.getAbsolutePath());
        String expectedFilePath = tempDir.getAbsolutePath() + "application-prod.properties";

        assertEquals(expectedFilePath, resultFilePath);

        File resultFile = new File(resultFilePath);
        assertTrue(resultFile.exists());
    }

}