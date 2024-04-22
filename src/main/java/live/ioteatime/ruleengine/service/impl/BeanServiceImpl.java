package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.BeanSet;
import live.ioteatime.ruleengine.service.BeanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
@Slf4j
public class BeanServiceImpl implements BeanService {
    @Override
    public File createConfig(BeanSet beanSet,String path) {
        String fileName = "application-"+beanSet.getMqttId()+".properties";
        File file = new File(path+fileName);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("spring.config.name=" + beanSet.getMqttId() + "\n");
            writer.write("mqtt.server.uri=" + beanSet.getMqttHost() + "\n");
            writer.write("mqtt.client.id=" + beanSet.getMqttId() + "\n");
            writer.write("mqtt.subscribe.topic=" + beanSet.getMqttTopic() + "\n");
            writer.write("spring.rabbitmq.host=" + beanSet.getRabbitmqHost() + "\n");
            writer.write("spring.rabbitmq.port=" + beanSet.getRabbitmqPort() + "\n");
            writer.write("spring.rabbitmq.username=" + beanSet.getRabbitmqUsername() + "\n");
            writer.write("spring.rabbitmq.password=" + beanSet.getRabbitmqPassword() + "\n");
            writer.write("spring.rabbitmq.template.exchange=" + beanSet.getRabbitmqExchange() + "\n");
            writer.write("spring.rabbitmq.template.routing-key=" + beanSet.getRabbitmqRoutingKey() + "\n");

        } catch (IOException e) {
            log.error("Create FIle false {}", e.getMessage());
        }
        return file;
    }
}
