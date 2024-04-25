package live.ioteatime.ruleengine.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import live.ioteatime.ruleengine.properties.JschProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class JschServiceImplTest {

    @Mock
    private JschProperties jschProperties;
    @Mock
    private JSch jSch;

    @Mock
    private Session session;

    @Mock
    private ChannelSftp channelSftp;

    @InjectMocks
    private JschServiceImpl jschService;


    @BeforeEach
    void setUp() throws Exception {
//      jschProperties = Mockito.mock(JschProperties.class);
//      jSch = Mockito.mock(JSch.class);
//      session = Mockito.mock(Session.class);
//      channelSftp = Mockito.mock(ChannelSftp.class);
//      jschService = new JschServiceImpl(jschProperties, channelSftp, session);
        MockitoAnnotations.openMocks(this);
        String folderPath = "local/folder/path";
        String fileName = "file.txt";
        String newDirectory = "remote/folder/path/" + fileName;

        when(jschProperties.getSavePath()).thenReturn("remote/folder/path");
        when(jschProperties.getPrivateKey()).thenReturn("privatekey");
        when(jschProperties.getUser()).thenReturn("username");
        when(jschProperties.getHost()).thenReturn("hostname");

        when(jSch.getSession(anyString(), anyString(), anyInt())).thenReturn(session);
        doNothing().when(session).setConfig(anyString(), anyString());
        doNothing().when(session).connect();

        when(session.openChannel("sftp")).thenReturn(channelSftp);
        doNothing().when(channelSftp).connect();
        doNothing().when(channelSftp).disconnect();
        doNothing().when(channelSftp).put(anyString(), anyString());
        doNothing().when(channelSftp).mkdir(newDirectory);

//        jschService = new JschServiceImpl(jschProperties, channelSftp, session);
    }

    @Test
    void scpFile() throws Exception {






        verify(channelSftp).mkdir(any());
        verify(channelSftp).put(anyString(), anyString());
        verify(channelSftp).disconnect();
        verify(session).disconnect();
    }
}