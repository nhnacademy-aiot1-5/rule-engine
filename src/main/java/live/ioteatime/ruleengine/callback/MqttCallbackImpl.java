package live.ioteatime.ruleengine.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.BlockingQueue;
import live.ioteatime.ruleengine.domain.MqttData;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

/**
 * MQTT 메세지를 수신하면 DTO로 매핑하고 큐에 담습니다.
 */
@Component
@Slf4j
public class MqttCallbackImpl implements MqttCallback {

    private final ObjectMapper objectMapper;
    private final BlockingQueue<MqttData> queue;

    /**
     * MQTT 콜백을 생성합니다.
     * <br>
     * 생성 후에 MQTT 클라이언트에 콜백을 등록해야합니다.
     *
     * @param objectMapper DTO로 매핑하기 위한 매퍼입니다.
     * @param queue        DTO를 담을 큐입니다.
     */
    public MqttCallbackImpl(ObjectMapper objectMapper, BlockingQueue<MqttData> queue) {
        this.objectMapper = objectMapper;
        this.queue = queue;
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.debug("Connection lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        MqttData mqttData = objectMapper.readValue(message.getPayload(), MqttData.class);
        mqttData.setTopic(topic);
        queue.put(mqttData);
        log.debug("Message arrived: {}", topic);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("deliveryComplete");
    }
}
