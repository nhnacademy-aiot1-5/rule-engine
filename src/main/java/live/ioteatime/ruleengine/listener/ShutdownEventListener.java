package live.ioteatime.ruleengine.listener;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ShutdownEventListener implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            ChannelSftp channelSftp = event.getApplicationContext().getBean(ChannelSftp.class);
            Session session = event.getApplicationContext().getBean(Session.class);
            ChannelExec channelExec = event.getApplicationContext().getBean(ChannelExec.class);
            channelSftp.disconnect();
            session.disconnect();
            channelExec.disconnect();
        } catch (BeansException e) {
            log.error("ShutdownEventListener {}", e.getMessage());
        }
        log.info("ShutdownEventListener onApplicationEvent");
    }
}
