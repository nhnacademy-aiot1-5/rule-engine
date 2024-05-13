package live.ioteatime.ruleengine.handler;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import live.ioteatime.ruleengine.handler.impl.MqttDataHandlerContextImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectFactory;

class MqttModbusDTOHandlerContextTest {

    @Test
    void start() {
        // given
        Integer total = 10;
        ObjectFactory<MqttDataHandler> factory = Mockito.mock(ObjectFactory.class);
        MqttDataHandler handler = Mockito.mock(MqttDataHandler.class);
        given(factory.getObject()).willReturn(handler);

        // when
        new MqttDataHandlerContextImpl(factory, total);

        // then
        verify(factory, Mockito.times(total)).getObject();
        verify(handler, Mockito.times(total)).start();
    }
}
