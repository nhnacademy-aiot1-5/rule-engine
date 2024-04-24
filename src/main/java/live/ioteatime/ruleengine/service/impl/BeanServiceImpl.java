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
            writer.write("spring.rabbitmq.host=" + beanSet.getRabbitmqHost() + "\n");
            writer.write("spring.rabbitmq.port=" + beanSet.getRabbitmqPort() + "\n");
            writer.write("spring.rabbitmq.username=" + beanSet.getRabbitmqUsername() + "\n");
            writer.write("spring.rabbitmq.password=" + beanSet.getRabbitmqPassword() + "\n");
            writer.write("spring.rabbitmq.template.exchange=" + beanSet.getRabbitmqExchange() + "\n");
            writer.write("spring.rabbitmq.template.routing-key=" + beanSet.getRabbitmqRoutingKey() + "\n");

        } catch (IOException e) {
            log.error("Create FIle false {}", e.getMessage());
        }
        return file.getPath();
    }

    /**
     *
     * @param name - 생성할 디렉토리 이름
     * @param path - 생성할 디렉토리 경로
     * @return 생성한 디렉토리 경로
     *  브로커 설정 파일을 담을 디렉토리를 만드는 메서드
     */
    @Override
    public String createFolder(String name, String path) {
        String folderPath = path+name;
        File file = new File(folderPath);
        boolean folderCreated = file.mkdirs();
        if (!folderCreated) {
            log.info("exists folder {}", folderPath);
        }
        log.info("crate folder {}", folderPath);
        return folderPath+"/";
    }

    /**
     *
     * @param filePath - 생성한 파일 경로
     * @return 파일의 이름
     *  필요없는 부분을 자르고 이름만 만드는 메서드
     */
    @Override
    public String splitFileName(String filePath) {
        String[] split = filePath.split("/");
        return split[split.length-1];
    }
}
