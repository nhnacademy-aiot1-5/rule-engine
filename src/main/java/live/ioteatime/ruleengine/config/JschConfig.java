package live.ioteatime.ruleengine.config;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import live.ioteatime.ruleengine.properties.JschProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JschConfig {
    private final JschProperties jschProperties;

    @Bean
    public JSch jSch() throws JSchException {
        JSch jSch = new JSch();
        jSch.addIdentity(jschProperties.getPrivateKey());
        return jSch;
    }

    //TODO Session Manager
    @Bean
    public Session session(JSch jSch) throws JSchException {
        Session session = jSch.getSession(jschProperties.getUser(), jschProperties.getHost(), 22);
        session.setConfig("StrictHostKeyChecking","no");
        session.connect();
        log.info("session connect user name {} ",session.getUserName());
        log.info("session connect  Host {} ",session.getHost());
        return session;
    }

    //TODO Channel Adaptor
    // boolean scp(session,filepath,destination) , void ssh(session)
    @Bean
    public ChannelSftp channelSftp(Session session) throws JSchException {
        ChannelSftp  channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        log.info("Channel connected.");
        return channel;
    }

    @Bean
    public ChannelExec channel(Session session) throws JSchException {
        Channel channel = session.openChannel("exec");
        ChannelExec channelExec = (ChannelExec) channel;
        channelExec.setPty(true);
        return channelExec;
    }
}
