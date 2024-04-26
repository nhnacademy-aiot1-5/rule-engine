package live.ioteatime.ruleengine.manager;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import live.ioteatime.ruleengine.properties.JschProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@Slf4j
public class JSchManager {
    private final JschProperties jschProperties;
    private Session session;
    private ChannelSftp channelSftp;
    private ChannelExec channelExec;

    /**
     * JSch 초기화 메서드
     */
    public void setJSch()  {
        try {
            JSch jSch = new JSch();
            jSch.addIdentity(jschProperties.getPrivateKey());
            session = jSch.getSession(jschProperties.getUser(), jschProperties.getHost(), 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            log.info("Session connected. User: {}, Host: {}", session.getUserName(), session.getHost());
        } catch (JSchException e) {
            log.error("Jsch init Failed {}", e.getMessage());
        }
    }

    /**
     *  scp 하기위한 인스턴스와의 연결 메소드
     * @return ChannelSftp
     */
    public ChannelSftp createChannelSftp() {
        try {
             channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            log.info("ChannelSftp connected.");
            return channelSftp;
        } catch (JSchException e) {
            log.error("create ChannelSftp Failed {}", e.getMessage());
            return null;
        }
    }

    /**
     *  인스턴스의 startup.sh 를 실행시키기위한 연결
     * @return ChannelExec
     */
    public ChannelExec createChannelExec() {
        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setPty(true);
            log.info("ChannelExec connected.");
            return channelExec;
        } catch (JSchException e) {
            log.error("createChannelExec Failed {}", e.getMessage());
            return null;
        }
    }

    /**
     * 모든 채널 연결 해제 메소드
     */
    public void disconnect() {
        if (channelSftp != null) {
            channelSftp.disconnect();
        }
        if (channelExec != null) {
            channelExec.disconnect();
        }
        log.info("Channel disconnected.");
    }

}
