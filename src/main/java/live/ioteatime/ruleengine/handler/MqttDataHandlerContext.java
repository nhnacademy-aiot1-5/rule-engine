package live.ioteatime.ruleengine.handler;

/**
 * MQTT 데이터 핸들러를 관리하는 컨텍스트 인터페이스입니다.
 */
public interface MqttDataHandlerContext {

    /**
     * 워커 스레드들을 일시 정지 시키는 메소드
     */
    void pauseAll();

    /**
     * 정지한 스레드들을 재 시작 하는
     */
    void restartAll();
}
