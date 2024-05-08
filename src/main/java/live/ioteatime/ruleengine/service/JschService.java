package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.exception.CreateJSchSessionException;

public interface JschService {

    /** 목적지 인스턴스에 설정 파일을 scp 하는 메소드
     * @param filePath 보낼 파일의 경로
     * @param fileName 저장할 인스턴스의 디렉토리의 이름
     * @param type 설정파일의 타입
     */
    void scpFile(String filePath, String fileName,String type) throws CreateJSchSessionException;

    /**
     *  bridge 제거 메소드
     * @param bridgeName 삭제할 브릿지 이름
     * @throws CreateJSchSessionException 에러
     */
    void deleteBridge(String type,String bridgeName) throws CreateJSchSessionException;
}
