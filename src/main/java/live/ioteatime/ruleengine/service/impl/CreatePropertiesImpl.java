package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.ModbusInfo;
import live.ioteatime.ruleengine.domain.MqttInfo;
import live.ioteatime.ruleengine.service.CreateProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatePropertiesImpl implements CreateProperties {

    private static final String LOGGING_CREATE_FILE_FAIL = "Create mqtt FIle false {}";

    private final RabbitProperties rabbitProperties;

    @Value("${create.folder.path}")
    private String path;

    @Override
    public String createConfig(MqttInfo mqttInfo) {
        String fileName = "application-prod.properties";
        File file = new File(path + fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("spring.config.name=prod" + "\n");
            writer.write("bridge.server.protocol=mqtt" + "\n");
            writer.write("mqtt.server.uri=" + mqttInfo.getMqttHost() + "\n");
            writer.write("mqtt.client.id=" + mqttInfo.getMqttId() + "\n");
            writer.write("mqtt.subscribe.topics=" + splitTopic(mqttInfo.getMqttTopic()) + "\n");
            rabbitmqConfigSet(writer);
        } catch (IOException e) {
            createFileFail(e);
        }

        return file.getPath();
    }

    @Override
    public String createConfig(ModbusInfo modbus) {
        String fileName = "application-prod.properties";
        File file = new File(path + fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("spring.config.name=prod" + "\n");
            writer.write("bridge.server.protocol=modbus" + "\n");
            writer.write("modbus.server.uri=" + modbus.getHost() + "\n");
            writer.write("modbus.request.channels=" + modbus.getChannel() + "\n");
            rabbitmqConfigSet(writer);
        } catch (IOException e) {
            createFileFail(e);
        }

        return file.getPath();
    }

    private static void createFileFail(IOException e) {
        log.error(LOGGING_CREATE_FILE_FAIL, e.getMessage());
    }

    private void rabbitmqConfigSet(BufferedWriter writer) throws IOException {
        writer.write("spring.rabbitmq.host=" + rabbitProperties.getHost() + "\n");
        writer.write("spring.rabbitmq.port=" + rabbitProperties.getPort() + "\n");
        writer.write("spring.rabbitmq.username=" + rabbitProperties.getUsername() + "\n");
        writer.write("spring.rabbitmq.password=" + rabbitProperties.getPassword() + "\n");
        writer.write("spring.rabbitmq.template.exchange=" + rabbitProperties.getTemplate().getExchange() + "\n");
        writer.write("spring.rabbitmq.template.routing-key=" + rabbitProperties.getTemplate().getRoutingKey() + "\n");
    }

    private String splitTopic(List<String> topics) {
        String topic = topics.toString();
        String[] parts = topic.substring(1,topic.length()-1).split(",");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            result.append(part.trim()).append(",");
        }

        result.deleteCharAt(result.length() - 1);

        return result.toString();
    }
}
