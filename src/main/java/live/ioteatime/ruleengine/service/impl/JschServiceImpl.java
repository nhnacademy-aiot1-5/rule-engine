package live.ioteatime.ruleengine.service.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import live.ioteatime.ruleengine.exception.CreateJSchSessionException;
import live.ioteatime.ruleengine.manager.JSchManager;
import live.ioteatime.ruleengine.properties.JschProperties;
import live.ioteatime.ruleengine.service.JschService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class JschServiceImpl implements JschService {

    private static final String LOGGING_SCP_ERROR = "scpFile Error {}";
    private static final String LOGGING_DELETE_BRIDGE_ERROR = "deleteBridge Error {}";
    private static final String LOGGING_DELETE_BRIDGE = "deleteBridge done";
    private static final String LOGGING_SCRIPT_MESSAGE = "exit-status {}";
    private static final String LOGGING_SCP_EXIST = "This setting already exists. overlay";
    private static final String LOGGING_PUT_FILE = "upload {}";
    private static final String LOGGING_PUT_FILE_ERROR = "uploadInstance {}";
    private static final String LOGGING_DELETE_FILE= "deleteFile {}";
    private static final String LOGGING_COMMAND= "giveCommand {}";
    private static final String LOGGING_DISCONNECT_JSCH= "JSch disconnected";

    @Value("${jsch.start.shell}")
    private String startShell;
    @Value("${jsch.stop.shell}")
    private String stopShell;

    private final JschProperties jschProperties;
    private final JSchManager jSchManager;

    @Override
    public void scpFile(String filePath, String fileName,String type) throws CreateJSchSessionException {
        String destinationDir = jschProperties.getSavePath()+"/"+type + "/" + fileName;
        Session session = jSchManager.createSession();
        ChannelSftp channelSftp = jSchManager.createChannelSftp(session);
        ChannelExec channelExec = jSchManager.createChannelExec(session);

        try {
            mkdirAndPut(filePath, channelSftp, destinationDir);
            deleteFile(filePath);
            giveCommand(startShell, type, fileName, channelExec);
            scriptMessage(channelExec);
            jschDisconnect(session, channelSftp, channelExec);
        } catch (Exception e) {
            log.error(LOGGING_SCP_ERROR, e.getMessage());
        }
    }

    @Override
    public void deleteBridge(String type,String bridgeName) throws CreateJSchSessionException {
        Session session = jSchManager.createSession();
        ChannelExec channelExec = jSchManager.createChannelExec(session);

        try {
            giveCommand(stopShell, type,bridgeName, channelExec);
            scriptMessage(channelExec);
            channelExec.disconnect();
            session.disconnect();
            log.info(LOGGING_DELETE_BRIDGE);
        } catch (Exception e) {
            log.error(LOGGING_DELETE_BRIDGE_ERROR, e.getMessage());
        }
    }

    private void scriptMessage(ChannelExec channelExec) throws IOException {
        InputStream in = channelExec.getInputStream();
        byte[] buffer = new byte[1024];
        int i;

        while ((i = in.read(buffer)) != -1) {
            if (channelExec.isClosed()) {
                log.info("{}", new String(buffer, 0, i));
                log.info(LOGGING_SCRIPT_MESSAGE, channelExec.getExitStatus());
                break;
            }
            safeSleep();
        }
    }

    private void safeSleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * @param filePath       보낼 파일의 경로
     * @param channel        연결한 인스턴스의 채널
     * @param destinationDir 인스턴스에 생성할 디렉토리 경로
     */
    private void mkdirAndPut(String filePath, ChannelSftp channel, String destinationDir) {
        try {
            channel.mkdir(destinationDir);
            putFile(filePath, destinationDir, channel);
        } catch (SftpException e) {
            log.info(LOGGING_SCP_EXIST);
            putFile(filePath, destinationDir, channel);
        }
    }

    private void putFile(String filePath, String destinationDir, ChannelSftp channel) {
        try {
            channel.put(filePath, destinationDir, ChannelSftp.OVERWRITE);
            log.info(LOGGING_PUT_FILE, filePath);
        } catch (SftpException e) {
            log.error(LOGGING_PUT_FILE_ERROR, e.getMessage());
        }
    }

    /**
     * @param filePath - 삭제할 파일 경로
     *                 작업 완료후 전송된 디렉토리, 파일을 삭제 하는 메서드
     */
    private void deleteFile(String filePath) throws IOException {
        Files.delete(Path.of(filePath));
        log.info(LOGGING_DELETE_FILE, filePath);
    }

    /**
     * @param fileName    실행 시킬 스크립트 경로
     * @param channelExec commandline 채널
     */
    private void giveCommand(String command,String type,String fileName, ChannelExec channelExec) throws JSchException {
        String commands = command + type + " " + fileName;
        channelExec.setCommand(commands);
        log.info(LOGGING_COMMAND, commands);
        channelExec.connect();
    }

    /**
     * 동작을 끝낸 세션과 채널을 닫기
     *
     * @param session     JSch 세션
     * @param channelSftp JSch 채널
     * @param channelExec JSch 채널
     */
    private void jschDisconnect(Session session, ChannelSftp channelSftp, ChannelExec channelExec) {
        session.disconnect();
        channelSftp.disconnect();
        channelExec.disconnect();
        log.info(LOGGING_DISCONNECT_JSCH);
    }
}
