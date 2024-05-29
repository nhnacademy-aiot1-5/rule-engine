package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.exception.CreateJSchSessionException;

public interface JschService {

    /** 목적지 인스턴스에 설정 파일을 scp 하는 메소드
     * 1.프로퍼티를 보낼 서버에 접근하여 지정한 경로 + mqtt,modbus 이름으로 디렉토리 생성
     * 2.현재 서버에 만들어진 파일 제거
     * 3.파일 전송 후 지정한 스크립트 실행
     * 4.보낸 서버에 로직대로 실행된 script 내용 받아와 띄움
     * 5.session,channel 연결 해제
     * @param filePath 보낼 파일의 경로
     * @param fileName 저장할 인스턴스의 디렉토리의 이름
     * @param type 설정파일의 타입
     */
    void scpFile(String filePath, String fileName,String type) throws CreateJSchSessionException;

    /**
     *  bridge 제거 메소드
     *  목표 서버의 삭제 스크립트 실행
     *  session,channel 연결 해제
     * @param bridgeName 삭제할 브릿지 이름
     * @param type 삭제할 브릿지 타입 ex)mqtt,modbus
     * @throws CreateJSchSessionException 에러
     */
    void deleteBridge(String type,String bridgeName) throws CreateJSchSessionException;
}
