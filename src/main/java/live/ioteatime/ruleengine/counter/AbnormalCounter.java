package live.ioteatime.ruleengine.counter;

import java.util.concurrent.Semaphore;
import org.springframework.stereotype.Component;

@Component
public class AbnormalCounter implements Runnable {

    private static final int DELAY = 60 * 1_000;

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
                return;
            }
            count++;
            semaphore.release();
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
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }
}
