package live.ioteatime.ruleengine.handler.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import live.ioteatime.ruleengine.handler.MqttDataHandler;
import live.ioteatime.ruleengine.handler.MqttDataHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MqttDataHandlerContextImpl implements MqttDataHandlerContext {

    private final List<MqttDataHandler> handlers;
    private final ObjectFactory<MqttDataHandler> factory;
    private final Integer totalThread;

    public MqttDataHandlerContextImpl(ObjectFactory<MqttDataHandler> factory,
        @Value("${datahandler.thread.total}") Integer totalThread) {
        handlers = new ArrayList<>();
        this.factory = factory;
        this.totalThread = totalThread;

        createHandlers();
        startHandlers();
    }

    private void startHandlers() {
        handlers.forEach(MqttDataHandler::start);
        log.info("Handlers have been started.");
    }

    private void createHandlers() {
        IntStream.range(0, totalThread)
                 .forEach(i -> handlers.add(factory.getObject()));
        log.info("Handlers have been created.");
    }
}
