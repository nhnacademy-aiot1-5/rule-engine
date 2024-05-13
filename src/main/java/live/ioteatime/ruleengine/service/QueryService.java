package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.QueryRequest;
import live.ioteatime.ruleengine.domain.QueryResponse;

import java.util.List;

public interface QueryService {

    /**
     * 요청 받은 쿼리를 InfluxQueryRepository 에 저장 해 주는 메소드
     * @param queryRequest 요청받은 쿼리
     */
    void addQuery(QueryRequest queryRequest);

    /**
     *  쿼리 리스트들을 가져오는 메소드
     * @return List<QueryResponse> 쿼리 목록들
     */
    List<QueryResponse> getQueries();

    /**
     * 쿼리를 제거 하는 메소드
     * @param index 요청받은 n번쨰 쿼리
     * @return String 지운 쿼리
     */
    String deleteQuery(int index);

    /**
     * influx 쿼리를 분해해서 태그 들로 나눔
     * 나눈 태그들을 mysql channel 테이블과 매칭
     * 매칭된 channel id 를 가져오는 메소드
     * @param index 가져올 influx 쿼리 번호
     * @return Tags 분리한 테그
     */
    int getChannelId(int index);
}
