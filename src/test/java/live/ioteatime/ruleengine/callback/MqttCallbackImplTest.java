package live.ioteatime.ruleengine.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import live.ioteatime.ruleengine.domain.MqttData;
import org.assertj.core.api.Assertions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MqttCallbackImplTest {

    private static final float VALUE = 12.34F;

    ObjectMapper objectMapper;
    BlockingQueue<MqttData> queue;
    MqttCallbackImpl mqttCallback;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        queue = new LinkedBlockingQueue<>();
        mqttCallback = new MqttCallbackImpl(objectMapper, queue);
    }

    @Test
    void messageArrived() throws Exception {
        // given
        String topic = "test/topic";
        MqttMessage message = getMessage();

        // when
        mqttCallback.messageArrived(topic, message);

        // then
        Assertions.assertThat(queue).element(0)
                  .hasFieldOrPropertyWithValue("topic", topic)
                  .hasFieldOrPropertyWithValue("value", VALUE);
    }

    private MqttMessage getMessage() throws Exception {
        MqttMessage mqttMessage = new MqttMessage();
        long time = System.currentTimeMillis();

        Map<String, Object> dto = new HashMap<>();
        dto.put("time", time);
        dto.put("value", VALUE);

        byte[] payload = objectMapper.writeValueAsBytes(dto);
        mqttMessage.setPayload(payload);

        return mqttMessage;
    }
}
