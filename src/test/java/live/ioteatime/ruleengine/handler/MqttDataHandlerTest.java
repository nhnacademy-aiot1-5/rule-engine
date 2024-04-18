package live.ioteatime.ruleengine.handler;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Constructor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import live.ioteatime.ruleengine.domain.MqttData;
import live.ioteatime.ruleengine.handler.impl.MqttDataHandlerImpl;
import live.ioteatime.ruleengine.rule.RuleChain;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MqttDataHandlerTest {

    BlockingQueue<MqttData> queue;
    RuleChain ruleChain;
    MqttDataHandler mqttDataHandler;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        queue = new LinkedBlockingQueue<>();
        ruleChain = Mockito.mock(RuleChain.class);
        mqttDataHandler = new MqttDataHandlerImpl(queue, ruleChain);
        objectMapper = new ObjectMapper();
    }

    @Test
    void run() throws Exception {
        // given
        MqttData mqttData = getMqttData();
        queue.put(mqttData);

        // when
        mqttDataHandler.start();
        Thread.sleep(100);
        mqttDataHandler.stop();

        // then
        Assertions.assertThat(queue).isEmpty();
        Mockito.verify(ruleChain).resetThreadLocal();
        Mockito.verify(ruleChain).doProcess(any());
    }

    private MqttData getMqttData() throws Exception {
        Constructor<MqttData> constructor = MqttData.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        return constructor.newInstance();
    }
}
