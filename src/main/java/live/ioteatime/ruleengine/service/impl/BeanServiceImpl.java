package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.BeanSet;
import live.ioteatime.ruleengine.service.BeanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BeanServiceImpl implements BeanService {
    private final RabbitProperties rabbitProperties;
    /**
     *
     * @param beanSet - 브로커 설정을 위한 변수들
     * @param path - 저장할 경로
     * @return - 생성한 파일 경로
     *  브로커 추가를 위한 설정 파일을 만드는 메서드
     */
    @Override
    public String createConfig(BeanSet beanSet,String path) {
        String fileName = "application-prod.properties";
        File file = new File(path+fileName);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("spring.config.name=prod" + "\n");
            writer.write("mqtt.server.uri=" + beanSet.getMqttHost() + "\n");
            writer.write("mqtt.client.id=" + beanSet.getMqttId() + "\n");
            writer.write("mqtt.subscribe.topic=" + beanSet.getMqttTopic() + "\n");
            writer.write("spring.rabbitmq.host=" + rabbitProperties.getHost() + "\n");
            writer.write("spring.rabbitmq.port=" + rabbitProperties.getPort() + "\n");
            writer.write("spring.rabbitmq.username=" + rabbitProperties.getUsername() + "\n");
            writer.write("spring.rabbitmq.password=" + rabbitProperties.getPassword() + "\n");
            writer.write("spring.rabbitmq.template.exchange=" + rabbitProperties.getTemplate().getExchange() + "\n");
            writer.write("spring.rabbitmq.template.routing-key=" + rabbitProperties.getTemplate().getRoutingKey() + "\n");

        } catch (IOException e) {
            log.error("Create FIle false {}", e.getMessage());
        }
        return file.getPath();
    }
}
