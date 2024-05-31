package live.ioteatime.ruleengine.counter;

import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AbnormalCounter implements Runnable {

    private static final int DELAY = 60 * 1_000;
    private static final String ABNORMAL_COUNTER = "Abnormal counter: {}";
    private static final String ABNORMAL_DATA_DETECTED = "Abnormal data detected!";

    private final Semaphore semaphore;

    private Integer count;

    public AbnormalCounter() {
        semaphore = new Semaphore(1);
        count = 0;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            decrease();
            delay();
        }
    }

    private void delay() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    public void increase() {
        try {
            semaphore.acquire();
            if (count > 5) {
                // API 호출
                count = 0;
                semaphore.release();
                log.warn(ABNORMAL_DATA_DETECTED);
                return;
            }
            count++;
            semaphore.release();
            log.debug(ABNORMAL_COUNTER, count);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    public void decrease() {
        try {
            semaphore.acquire();
            if (count <= 0) {
                semaphore.release();
                return;
            }
            count--;
            semaphore.release();
            log.debug(ABNORMAL_COUNTER, count);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }
}
