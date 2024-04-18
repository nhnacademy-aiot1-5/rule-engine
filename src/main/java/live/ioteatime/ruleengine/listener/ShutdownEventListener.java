package live.ioteatime.ruleengine.listener;

import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShutdownEventListener implements ApplicationListener<ContextClosedEvent> {

    private final MqttClient mqttClient;
    private final InfluxDBClient influxDBClient;

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
        closeMqttClient();
        closeInfluxDBClient();
    }

    private void closeInfluxDBClient() {
        influxDBClient.close();
        log.info("InfluxDBClient disconnected.");
    }

    private void closeMqttClient() {
        try {
            mqttClient.disconnect();
            mqttClient.close();
            log.info("MqttClient disconnected.");
        } catch (MqttException ignore) {
            // ignore
        }
    }
}
