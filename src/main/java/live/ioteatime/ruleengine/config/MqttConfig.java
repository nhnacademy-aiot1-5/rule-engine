package live.ioteatime.ruleengine.config;

import live.ioteatime.ruleengine.properties.MqttClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MqttConfig {

    @Bean
    public MqttClient mqttClient(MqttClientProperties mqttClientProperties,
        MqttCallback mqttCallback) throws MqttException {
        MqttClient mqttClient = new MqttClient(mqttClientProperties.getUri(), mqttClientProperties.getId());
        mqttClient.setCallback(mqttCallback);

        mqttClient.connect();
        log.info("mqttClient connect success: " + mqttClientProperties.getUri());
        mqttClient.subscribe(mqttClientProperties.getTopic());
        log.info("mqttClient subscribe success: " + mqttClientProperties.getTopic());

        return mqttClient;
    }
}
