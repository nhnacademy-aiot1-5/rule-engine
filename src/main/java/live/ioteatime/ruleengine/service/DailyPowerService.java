package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.LocalMidnightDto;

import java.time.LocalDateTime;
import java.util.Map;

public interface DailyPowerService {

    /**
     * 저장 되어 있는 쿼리문의 갯수
     * @return 쿼리문의 갯수
     */
    int getQueryCount();

    /**
     *  저장된 쿼리문 불러오는 메소드
     * @param index 몇번쨰 쿼리
     * @return influx 쿼리문
     */
    String getQuery(int index);

    /**
     * influxdb 에 하루치 전력 총 합을 가져와 로컬 시간으로 바꾸는 메소드
     *
     * @param dailyQuery 쿼리문
     * @return Map<LocalDateTime, Object> 로컬 시간으로 바꾼 맵
     */
    Map<LocalDateTime, Object> convertInfluxDbResponseToLocalTime(String dailyQuery);

    /**
     * 하루치 총 전력을 계산 하는 메소드
     * 총 전력량 에서 전날의 전력량을 빼기
     *
     * @param hourlyPowerDataMap 시간과 값이 들어있는 맵
     * @param midNights          전날과 그날의 00:00
     * @return double
     */
    double calculateDailyPower(Map<LocalDateTime, Object> hourlyPowerDataMap, LocalMidnightDto midNights);

    void insertMysql(LocalDateTime yesterday, Double totalPower,int channelId,Double bill);
}
