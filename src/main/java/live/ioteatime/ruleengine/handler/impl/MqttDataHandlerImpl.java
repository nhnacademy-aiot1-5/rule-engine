package live.ioteatime.ruleengine.handler.impl;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.handler.MqttDataHandler;
import live.ioteatime.ruleengine.rule.RuleChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MQTT 데이터 핸들러입니다.
 * <p>
 * 큐에서 데이터를 꺼내 룰체인을 사용하여 데이터를 처리합니다.
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MqttDataHandlerImpl implements MqttDataHandler {
    private static final String LOGGING_CREATE_THREAD = "MqttDataHandlerThread-";
    private static final String LOGGING_PAUSE_THREAD = "MqttDataHandlerThread is paused {}";
    private static final String LOGGING_START_THREAD = "{} has been started.";
    private static final String LOGGING_STOP_THREAD = "{} has been stopped.";
    private static final String LOGGING_REQUEST_THREAD = "requesting {} to start.";
    private static final String LOGGING_INTERRUPT_THREAD = "interrupting {}";
    private static final String LOGGING_RESTARTING_THREAD = "restarting {}";

    private static final AtomicInteger counter = new AtomicInteger(0);

    private final Thread thread;
    private final BlockingQueue<MqttModbusDTO> blockingQueue;
    private final RuleChain ruleChain;
    private boolean paused = false;
    private final Object pauseLock = new Object(); // 별도의 모니터 객체

    /**
     * 핸들러를 생성합니다.
     *
     * @param blockingQueue MQTT 데이터가 저장된 큐입니다.
     * @param ruleChain     데이터를 처리할 룰 체인입니다.
     */
    public MqttDataHandlerImpl(BlockingQueue<MqttModbusDTO> blockingQueue, RuleChain ruleChain) {
        thread = new Thread(this, LOGGING_CREATE_THREAD + counter.incrementAndGet());
        this.blockingQueue = blockingQueue;
        this.ruleChain = ruleChain;
    }

    private void handleData() throws InterruptedException {
        MqttModbusDTO mqttModbusDTO = blockingQueue.take();
        ruleChain.resetThreadLocal();
        ruleChain.doProcess(mqttModbusDTO);
    }

    @Override
    public void run() {
        preProcess();
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (pauseLock) {
                while (paused) {
                    try {
                        pauseLock.wait();
                        log.debug(LOGGING_PAUSE_THREAD, thread.getName());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            process();
        }
        postProcess();
    }

    private void preProcess() {
        log.debug(LOGGING_START_THREAD, thread.getName());
    }

    private void process() {
        try {
            handleData();
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    private void postProcess() {
        log.debug(LOGGING_STOP_THREAD, thread.getName());
    }

    @Override
    public void start() {
        thread.start();
        log.debug(LOGGING_REQUEST_THREAD, thread.getName());
    }

    @Override
    public void stop() {
        thread.interrupt();
        log.debug(LOGGING_INTERRUPT_THREAD, thread.getName());
    }

    @Override
    public void pause() {
        synchronized (pauseLock) {
            paused = true;
        }
    }

    @Override
    public void reStart() {
        synchronized (pauseLock) {
            pauseLock.notifyAll();
            paused = false;
            log.debug(LOGGING_RESTARTING_THREAD, thread.getName());
        }
    }

    @Override
    public boolean isWait() {
        return Thread.State.WAITING.equals(thread.getState());
    }

}
