package live.ioteatime.ruleengine.handler;

import live.ioteatime.ruleengine.handler.impl.MqttDataHandlerContextImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectFactory;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class MqttModbusDTOHandlerContextTest {
    ObjectFactory<MqttDataHandler> factory;
    MqttDataHandler handler;
    Integer total = 10;

    @BeforeEach
    void setUp() {
        factory = Mockito.mock(ObjectFactory.class);
        handler = Mockito.mock(MqttDataHandler.class);
        given(factory.getObject()).willReturn(handler);
    }

    @Test
    void start() {
        new MqttDataHandlerContextImpl(factory, total);

        verify(factory, Mockito.times(total)).getObject();
        verify(handler, Mockito.times(total)).start();
    }

    @Test
    void pauseAllTest() {
        MqttDataHandlerContextImpl mqttDataHandlerContext = new MqttDataHandlerContextImpl(factory, total);

        when(handler.isWait()).thenReturn(true);

        mqttDataHandlerContext.pauseAll();

        verify(handler, times(total)).pause();
        verify(handler, times(total)).isWait();
    }

    @Test
    void restartTest() {
        MqttDataHandlerContextImpl mqttDataHandlerContext = new MqttDataHandlerContextImpl(factory, total);

        when(handler.isWait()).thenReturn(true);

        mqttDataHandlerContext.restartAll();

        verify(handler, times(total)).reStart();
    }
}
