package live.ioteatime.ruleengine.manager;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import live.ioteatime.ruleengine.exception.CreateJSchSessionException;
import live.ioteatime.ruleengine.properties.JschProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class JSchManager {
    private final JschProperties jschProperties;
    private final JSch jSch = new JSch();

    /**
     * 처음 빈 생성 직후 자동으로 호출
     * 정상적으로 세션이 연결 되는지 확인
     */
    @PostConstruct
    private void sessionCheck() throws CreateJSchSessionException {
        Session session = createSession();
        if (session == null || !session.isConnected()) {
            log.error("session check failed");

            return;
        }
        log.info("Session connected. User: {}, Host: {}", session.getUserName(), session.getHost());
        log.info("session check success");
        session.disconnect();
    }

    /**
     * JSch 초기화 후 세션 생성
     * @return Session
     */
    public Session createSession() throws CreateJSchSessionException {
        try {
            jSch.addIdentity(jschProperties.getPrivateKey());
            Session session = jSch.getSession(jschProperties.getUser(), jschProperties.getHost(), 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            return session;
        } catch (JSchException e) {
            throw new CreateJSchSessionException("create JSch session failed", e);
        }
    }

    /**
     * scp 하기위한 인스턴스와의 연결 메소드
     * @return ChannelSftp
     */
    public ChannelSftp createChannelSftp(Session session) {
        try {
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            log.info("ChannelSftp connected.");

            return channelSftp;
        } catch (JSchException e) {
            log.error("create ChannelSftp Failed {}", e.getMessage());

            return null;
        }
    }

    /**
     * 인스턴스의 startup.sh 를 실행시키기위한 연결
     * @return ChannelExec
     */
    public ChannelExec createChannelExec(Session session) {
        try {
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setPty(true);
            log.info("ChannelExec connected.");

            return channelExec;
        } catch (JSchException e) {
            log.error("createChannelExec Failed {}", e.getMessage());

            return null;
        }
    }

}
