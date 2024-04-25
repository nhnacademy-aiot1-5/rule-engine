package live.ioteatime.ruleengine.service.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import live.ioteatime.ruleengine.manager.JSchManager;
import live.ioteatime.ruleengine.properties.JschProperties;
import live.ioteatime.ruleengine.service.JschService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class JschServiceImpl implements JschService {
    private final JschProperties jschProperties;
    private final JSchManager jSchManager;

    public JschServiceImpl(JschProperties jschProperties, JSchManager jSchManager) {
        this.jschProperties = jschProperties;
        this.jSchManager = jSchManager;
        jSchManager.setJSch();
    }

    /**
     * @param filePath 저장할 인스턴스의 디레토리 주소
     * @param fileName 저장할 인스턴스의 디렉토리의 이름
     *                 저장할 인스턴스에 scp 보내는 메서드
     *
     */
    @Override
    public void scpFile(String filePath, String fileName) {
        String destination = jschProperties.getSavePath() + "/" + fileName;
        ChannelSftp channel = jSchManager.createChannelSftp();
        ChannelExec channelExec = jSchManager.createChannelExec();
        try {

            putInstance(filePath, channel, destination);

            deleteFolder(filePath);

            channelExec.setCommand("./startup.sh " + fileName);
            channelExec.connect();

        } catch (Exception e) {
            log.error("scpFile Error {}", e.getMessage());
        }
    }

    /**
     * @param filePath     - 파일 경로
     * @param channel - 채널
     * @param newDirectory - 저장할 인스턴스의 경로
     *                     디렉토리 경로에있는 디렉토리, 파일 모두 전송하는 메서드
     */
    private static void putInstance(String filePath, ChannelSftp channel, String newDirectory) {
        try {
            channel.mkdir(newDirectory);
            uploadInstance(filePath, newDirectory, channel);
        } catch (SftpException e) {
            log.info("이미 존재하는 설정입니다. 덮어 씌우기");
            uploadInstance(filePath, newDirectory, channel);
        }
    }

    private static void uploadInstance(String filePath, String newDirectory, ChannelSftp channel) {
        try {
            channel.put(filePath, newDirectory, ChannelSftp.OVERWRITE);
            log.info("upload {}", filePath);
        } catch (SftpException e) {
            log.error("uploadInstance {}", e.getMessage());
        }
    }

    /**
     * @param filePath - 삭제할 파일 경로
     *                   작업 완료후 전송된 디렉토리, 파일을 삭제 하는 메서드
     */
    private static void deleteFolder(String filePath) throws IOException {
        Files.delete(Path.of(filePath));
        log.info("deleteFile {}", filePath);
    }

}
