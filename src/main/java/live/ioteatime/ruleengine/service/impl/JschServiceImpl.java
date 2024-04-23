package live.ioteatime.ruleengine.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
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
    private ChannelSftp channel;
    private Session session;
    @Override
    public void scpFile(String folderPath,String fileName) {
        String newDirectory = jschProperties.getSavePath()+"/"+fileName;
        try {
            JSch jSch = new JSch();
            jSch.addIdentity(jschProperties.getKeyPath());
            session = jSch.getSession(jschProperties.getUser(), jschProperties.getHost(), 22);
            session.setConfig("StrictHostKeyChecking","no");
            session.connect();
            log.info("session connect {} ",session.getUserInfo());
             channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

                channel.mkdir(newDirectory);
                putInstance(folderPath, channel, newDirectory);
            deleteFolder(new File(folderPath));
            channel.disconnect();
            session.disconnect();

        }catch (SftpException e){
            putInstance(folderPath, channel, newDirectory);
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            log.error("scpFile  {} ", e.getMessage());
        }
    }

    private static void putInstance(String folderPath, ChannelSftp channel, String newDirectory) {
        File folder = new File(folderPath);
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                try {
                    channel.put(file.getAbsolutePath(), newDirectory);
                } catch (SftpException e) {
                    e.getStackTrace();
                }
            }
        }
    }
    private static void deleteFolder(File folder) {

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        folder.delete();
        log.info("delete folder {}", folder.getAbsolutePath());
    }
}
