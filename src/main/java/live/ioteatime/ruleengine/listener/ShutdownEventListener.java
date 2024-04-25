package live.ioteatime.ruleengine.listener;

import live.ioteatime.ruleengine.manager.JSchManager;
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
            JSchManager jSchManager = event.getApplicationContext().getBean(JSchManager.class);
            jSchManager.disconnect();
        } catch (BeansException e) {
            log.error("ShutdownEventListener {}", e.getMessage());
        }
        log.info("ShutdownEventListener onApplicationEvent");
    }
}
