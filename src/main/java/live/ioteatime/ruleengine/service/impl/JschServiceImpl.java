package live.ioteatime.ruleengine.service.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
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
    private final ChannelSftp channel;
    private final Session session;
    private final ChannelExec channelExec;
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

                putInstance(folderPath, channel, newDirectory);
                deleteFolder(folderPath);
                channelExec.setCommand("./startup.sh "+fileName);
                channelExec.connect();

                channel.disconnect();
                channelExec.disconnect();
                session.disconnect();

            } catch (Exception e) {
                e.getStackTrace();
            }
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
