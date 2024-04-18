package live.ioteatime.ruleengine.handler;

/**
 * 비동기적으로 데이터를 처리하기 위해 독립적인 스레드로 동작하는 핸들러 인터페이스입니다.
 */
public interface MqttDataHandler extends Runnable {

    /**
     * 핸들러에게 시작 요청을 보냅니다.
     */
    void start();

    /**
     * 핸들러에게 종료 요청을 보냅니다.
     */
    void stop();
}
