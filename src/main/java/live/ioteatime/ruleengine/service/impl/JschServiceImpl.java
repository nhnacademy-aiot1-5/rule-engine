package live.ioteatime.ruleengine.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import live.ioteatime.ruleengine.properties.JschProperties;
import live.ioteatime.ruleengine.service.JschService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class JschServiceImpl implements JschService {
    private final JschProperties jschProperties;

    /**
     *
     * @param folderPath 저장할 인스턴스의 디레토리 주소
     * @param fileName 저장할 인스턴스의 디렉토리의 이름
     *  저장할 인스턴스에 scp 보내는 메서드
     */
    @Override
    public void scpFile(String folderPath,String fileName) {
        String newDirectory = jschProperties.getSavePath()+"/"+fileName;
            try{
                JSch jSch = new JSch();
                jSch.addIdentity(jschProperties.getPrivateKey());
                Session session = createSession(jSch);

                ChannelSftp channel = createChannel(session);

                putInstance(folderPath, channel, newDirectory);
                deleteFolder(folderPath);

                channel.disconnect();
                session.disconnect();

            } catch (Exception e) {
                e.getStackTrace();
            }
    }

    /**
     *
     * @param jSch ssh 프로토콜 라이브러리
     * @return session -> ssh 연결을 나타내는 객체
     * @throws JSchException
     *  ssh 연결을 위한 session 객체를 만드는 메서드
     */
    private Session createSession(JSch jSch) throws JSchException {
        Session session = jSch.getSession(jschProperties.getUser(), jschProperties.getHost(), 22);
        session.setConfig("StrictHostKeyChecking","no");
        session.connect();
        log.info("session connect {} ",session.getUserName());
        return session;
    }

    /**
     *
     * @param session
     * @return channel
     * @throws JSchException
     *  ssh 프로토콜로 파일을 전송, 관리해 주는 채널인 ChannelSftp 를 만드는 메서드
     */
    private ChannelSftp createChannel(Session session) throws JSchException {
        ChannelSftp  channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        log.info("Channel connected.");
        return channel;
    }

    /**
     *
     * @param folderPath - 디렉토리 경로
     * @param channel
     * @param newDirectory - 저장할 인스턴스의 경로
     *  디렉토리 경로에있는 디렉토리, 파일 모두 전송하는 메서드
     */
    private static void putInstance(String folderPath, ChannelSftp channel, String newDirectory) {
        File folder = new File(folderPath);
        try {
            channel.mkdir(newDirectory);
            uploadInstance(folder.listFiles(),newDirectory,channel);
        } catch (SftpException e) {
            log.info("이미 있는 설정 파일입니다. 덮어 씌우기");
            uploadInstance(folder.listFiles(),newDirectory,channel);
        }
    }

    private static void uploadInstance(File[] files,String folderPath,ChannelSftp channel ){
        for (File file : Objects.requireNonNull(files)) {
            if (file.isFile()) {
                try {
                    channel.put(file.getAbsolutePath(), folderPath);
                    log.info("upload {}", file.getAbsolutePath());
                } catch (SftpException e) {
                    e.getStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param folderPath - 삭제할 디렉토리 경로
     * 작업 완료후 전송된 디렉토리, 파일을 삭제 하는 메서드
     */
    private static void deleteFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file.getAbsolutePath());
                }
            }
        }
        if (folder.delete()) {
        log.info("delete folder {}", folder.getAbsolutePath());
        }
        else {
            log.warn("Failed to delete folder {}", folder.getAbsolutePath());
        }
    }
}
