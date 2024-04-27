package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.exception.CreateJSchSessionException;

public interface JschService {

    /**
     * @param filePath 보낼 파일의 경로
     * @param fileName 저장할 인스턴스의 디렉토리의 이름
     */
    void scpFile(String filePath,String fileName) throws CreateJSchSessionException;
}
