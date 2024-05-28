package live.ioteatime.ruleengine.service.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import live.ioteatime.ruleengine.manager.JSchManager;
import live.ioteatime.ruleengine.properties.JschProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
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

    String destination = "destination";
    String filePath = "filePath";
    String fileName = "test";
    String type = "mqtt";

    @Test
    void scpFile() throws Exception {
        String destinationPath = destination + "/" + type + "/" + fileName;
        String command = "./startup.sh " + type + " " + fileName;

        when(properties.getSavePath()).thenReturn(destination);
        when(jSchManager.createSession()).thenReturn(session);
        when(jSchManager.createChannelExec(session)).thenReturn(channelExec);
        when(jSchManager.createChannelSftp(session)).thenReturn(channelSftp);
        when(channelExec.getInputStream()).thenReturn(new ByteArrayInputStream(command.getBytes()));

        try (MockedStatic<Files> files = Mockito.mockStatic(Files.class)) {
            jschService.scpFile(filePath, fileName, type);

            verify(channelSftp).mkdir(eq(destinationPath));
            verify(channelSftp).put(eq(filePath), eq(destinationPath), eq(ChannelSftp.OVERWRITE));
            files.verify(() -> Files.delete(eq(Path.of(filePath))));
            verify(channelExec).setCommand((command));
            verify(channelExec).connect();
            verify(channelExec).getInputStream();
            verify(channelExec).disconnect();
            verify(channelSftp).disconnect();
        }
    }

    @Test
    void deleteBridgeTest() throws Exception {
        String command = "./stop.sh " + type + " " + fileName;

        when(jSchManager.createSession()).thenReturn(session);
        when(jSchManager.createChannelExec(session)).thenReturn(channelExec);
        when(channelExec.getInputStream()).thenReturn(new ByteArrayInputStream(command.getBytes()));

        jschService.deleteBridge(type, fileName);

        verify(channelExec).setCommand(command);
        verify(channelExec).connect();
        verify(channelExec).getInputStream();
        verify(channelExec).disconnect();
    }

}
