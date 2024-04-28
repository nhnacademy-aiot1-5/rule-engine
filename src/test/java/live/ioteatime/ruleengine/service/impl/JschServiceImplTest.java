package live.ioteatime.ruleengine.service.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import live.ioteatime.ruleengine.manager.JSchManager;
import live.ioteatime.ruleengine.properties.JschProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JschServiceImplTest {

    @Mock
    JschProperties properties;
    @Mock
    JSchManager jSchManager;
    @Mock
    Session session;
    @Mock
    ChannelSftp channelSftp;
    @Mock
    ChannelExec channelExec;
    @InjectMocks
    JschServiceImpl jschService;
    MockedStatic<Files> files;

    @BeforeEach
    void setUp() {
        files = Mockito.mockStatic(Files.class);
    }

    @Test
    void scpFile() throws Exception {

        String destination = "destination";
        String filePath = "filePath";
        String fileName = "test";
        String shName = "./startup.sh ";
        String command = "./startup.sh " + fileName;
        String destinationPaht = destination + "/" + fileName;

        when(properties.getSavePath()).thenReturn(destination);
        when(jSchManager.createSession()).thenReturn(session);
        when(jSchManager.createChannelSftp(session)).thenReturn(channelSftp);
        when(jSchManager.createChannelExec(session)).thenReturn(channelExec);

        jschService.scpFile(filePath, fileName,shName);

        verify(channelSftp).mkdir(eq(destinationPaht));
        verify(channelSftp).put(eq(filePath), eq(destinationPaht), eq(ChannelSftp.OVERWRITE));
        files.verify(() -> Files.delete(eq(Path.of(filePath))));
        verify(channelExec).setCommand((command));
        verify(channelExec).connect();
    }

}
