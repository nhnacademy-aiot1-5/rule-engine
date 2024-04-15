package live.ioteatime.ruleengine.callback;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
public class MqttCallbackImpl implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        throw new UnsupportedOperationException("Unsupported connectionLost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        throw new UnsupportedOperationException("Unsupported messageArrived");
        // TODO MqttData 매핑
        // TODO Queue에 put
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        throw new UnsupportedOperationException("Unsupported deliveryComplete");
    }
}
