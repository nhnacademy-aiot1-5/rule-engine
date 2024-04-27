package live.ioteatime.ruleengine.service.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.JSchException;
import live.ioteatime.ruleengine.exception.CreateJSchSessionException;
import live.ioteatime.ruleengine.manager.JSchManager;
import live.ioteatime.ruleengine.properties.JschProperties;
import live.ioteatime.ruleengine.service.JschService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class JschServiceImpl implements JschService {
    private final JschProperties jschProperties;
    private final JSchManager jSchManager;

    @Override
    public void scpFile(String filePath, String fileName) throws CreateJSchSessionException {
        String destinationDir = jschProperties.getSavePath() + "/" + fileName;
        Session session = jSchManager.createSession();
        ChannelSftp channelSftp = jSchManager.createChannelSftp(session);
        ChannelExec channelExec = jSchManager.createChannelExec(session);

        try {
            mkdirAndPut(filePath, channelSftp, destinationDir);
            deleteFile(filePath);
            giveCommand(fileName, channelExec);
            jschDisconnect(session, channelSftp, channelExec);
        } catch (Exception e) {
            log.error("scpFile Error {}", e.getMessage());
        }
    }

    /**
     * @param filePath       보낼 파일의 경로
     * @param channel        연결한 인스턴스의 채널
     * @param destinationDir 인스턴스에 생성할 디렉토리 경로
     */
    private static void mkdirAndPut(String filePath, ChannelSftp channel, String destinationDir) {
        try {
            channel.mkdir(destinationDir);
            putFile(filePath, destinationDir, channel);
        } catch (SftpException e) {
            log.info("이미 존재하는 설정입니다. 덮어 씌우기");
            putFile(filePath, destinationDir, channel);
        }
    }

    private static void putFile(String filePath, String destinationDir, ChannelSftp channel) {
        try {
            channel.put(filePath, destinationDir, ChannelSftp.OVERWRITE);
            log.info("upload {}", filePath);
        } catch (SftpException e) {
            log.error("uploadInstance {}", e.getMessage());
        }
    }

    /**
     * @param filePath - 삭제할 파일 경로
     *                 작업 완료후 전송된 디렉토리, 파일을 삭제 하는 메서드
     */
    private static void deleteFile(String filePath) throws IOException {
        Files.delete(Path.of(filePath));
        log.info("deleteFile {}", filePath);
    }

    /**
     * @param scriptPath  실행 시킬 스크립트 경로
     * @param channelExec commandline 채널
     */
    private static void giveCommand(String scriptPath, ChannelExec channelExec) throws JSchException {
        channelExec.setCommand("./startup.sh " + scriptPath);
        channelExec.connect();
    }

    /**
     * 동작을 끝낸 세션과 채널을 닫기
     *
     * @param session     JSch 세션
     * @param channelSftp JSch 채널
     * @param channelExec JSch 채널
     */
    private static void jschDisconnect(Session session, ChannelSftp channelSftp, ChannelExec channelExec) {
        session.disconnect();
        channelSftp.disconnect();
        channelExec.disconnect();
        log.info("JSch disconnected");
    }

}
