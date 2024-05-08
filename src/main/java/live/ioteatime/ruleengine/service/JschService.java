package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.exception.CreateJSchSessionException;

public interface JschService {

    /**
     * @param filePath 보낼 파일의 경로
     * @param fileName 저장할 인스턴스의 디렉토리의 이름
     */
    void scpFile(String filePath, String fileName,String type) throws CreateJSchSessionException;

    /**
     *  bridge 제거 메소드
     * @param bridgeName 삭제할 브릿지 이름
     * @throws CreateJSchSessionException 에러
     */
    void deleteBridge(String bridgeName) throws CreateJSchSessionException;
}
