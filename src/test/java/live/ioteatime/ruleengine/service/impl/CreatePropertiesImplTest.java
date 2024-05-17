package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.ModbusInfo;
import live.ioteatime.ruleengine.domain.MqttInfo;
import live.ioteatime.ruleengine.service.CreateProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;

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

        createProperties = new CreatePropertiesImpl(rabbitPropertiesMock);
    }

    @Test
    void createMqttConfigTest() throws Exception {
        Constructor<MqttInfo> constructor = MqttInfo.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        MqttInfo mqttInfo = constructor.newInstance();
        List<String> topics = List.of("data/#", "data/#", "data/#");
        ReflectionTestUtils.setField(mqttInfo, "mqttHost", "localhost");
        ReflectionTestUtils.setField(mqttInfo, "mqttId", "test");
        ReflectionTestUtils.setField(mqttInfo, "mqttTopic", topics);

        String resultFilePath = createProperties.createConfig(mqttInfo);
        File resultFile = new File(resultFilePath);

        assertTrue(resultFile.exists());
    }

    @Test
    void createModbusConfigTest() throws Exception {
        Constructor<ModbusInfo> constructor = ModbusInfo.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ModbusInfo modbusInfo = constructor.newInstance();
        ReflectionTestUtils.setField(modbusInfo, "name", "localhost");
        ReflectionTestUtils.setField(modbusInfo, "host", "localhost");
        ReflectionTestUtils.setField(modbusInfo, "channel", "800/2,12/21");

        String config = createProperties.createConfig(modbusInfo);
        File resultFile = new File(config);

        assertTrue(resultFile.exists());
    }

}
