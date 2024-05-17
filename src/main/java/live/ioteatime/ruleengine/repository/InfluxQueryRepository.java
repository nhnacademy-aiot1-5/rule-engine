package live.ioteatime.ruleengine.repository;

import com.influxdb.query.FluxTable;

import java.util.List;

public interface InfluxQueryRepository {

    /**
     *  query 를 influxdb 날리는 메소드
     * @param query 원하는 쿼리
     * @return List<FluxTable> influxdb 의 응답
     */
    List<FluxTable> query(String query);

    /**
     *  현재 쿼리들의 개수
     * @return int 쿼리 개수
     */
    int getQuerySize();

    /**
     *  n 번째 쿼리 가져오는 메소드
     * @param index n 번째
     * @return String 쿼리
     */
    String getQuery(int index);

    /**
     *  저장되어 있는 쿼리를 repository 에 업데이트하는 메소드
     */
    void updateQuery();

    /**
     *  입력 받은 쿼리를 쿼리 저장소에 저장 하는 메소드
     * @param query 저장할 쿼리
     */
    void writeQuery(String query);

    /**
     *  쿼리 삭제 메소드 , 기존 쿼리 파일 제거, 다시 쓰기
     */
    void modifyQuery();

    /**
     *  n 번쨰 쿼리 repository 에서 제거 하는 메소드
     * @param index n 번째
     * @return String 제거한 쿼리
     */
    String removeQuery(int index);

    String getQueryPath();
}
