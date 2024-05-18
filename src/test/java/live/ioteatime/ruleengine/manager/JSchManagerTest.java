package live.ioteatime.ruleengine.manager;



import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import live.ioteatime.ruleengine.exception.CreateChannelExecException;
import live.ioteatime.ruleengine.exception.CreateChannelSftpException;
import live.ioteatime.ruleengine.exception.CreateJSchSessionException;
import live.ioteatime.ruleengine.properties.JschProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class JSchManagerTest {
    @Mock
    JSch jsch;
    @Mock
    JschProperties jschProperties;
    @Mock
    Session mockSession;
    @Mock
    ChannelSftp mockChannelSftp;
    @Mock
    ChannelExec mockChannelExec;
    @InjectMocks
    JSchManager jSchManager;

    @BeforeEach
    public void setUp() throws JSchException {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jSchManager, "jSch", jsch);
        ReflectionTestUtils.setField(jSchManager, "session", mockSession);
    }

    @Test
    void sessionCheck_SuccessfulTest() throws CreateJSchSessionException, JSchException {
        when(jsch.getSession(jschProperties.getUser(), jschProperties.getHost(), 22)).thenReturn(mockSession);
        when(mockSession.isConnected()).thenReturn(true);

        jSchManager.sessionCheck();

        verify(mockSession).connect();
        verify(mockSession).disconnect();
    }

    @Test
    void sessionCheck_FailedConnection() throws Exception {
        when(jsch.getSession(jschProperties.getUser(), jschProperties.getHost(), 22)).thenReturn(mockSession);
        when(mockSession.isConnected()).thenReturn(false);

        jSchManager.sessionCheck();

        verify(mockSession, never()).disconnect();
    }

    @Test
    void createSession_Successful() throws JSchException, CreateJSchSessionException {
        when(jschProperties.getPrivateKey()).thenReturn("private_key_path");
        when(jschProperties.getUser()).thenReturn("user");
        when(jschProperties.getHost()).thenReturn("host");
        when(jsch.getSession(jschProperties.getUser(), jschProperties.getHost(), 22)).thenReturn(mockSession);

        Session session = jSchManager.createSession();

        verify(mockSession).connect();
        assertEquals(mockSession, session);
    }

    @Test
    void createSession_ThrowsJSchException() throws Exception {
        when(jschProperties.getPrivateKey()).thenReturn("private_key_path");
        when(jschProperties.getUser()).thenReturn("user");
        when(jschProperties.getHost()).thenReturn("host");
        when(jsch.getSession(anyString(), anyString(), anyInt())).thenThrow(new JSchException());

        assertThrows(CreateJSchSessionException.class, () -> {
            jSchManager.createSession();
        });
    }

    @Test
    void createChannelSftp_SuccessfulTest() throws JSchException {
        String channelName = "sftp";

        when(mockSession.openChannel(channelName)).thenReturn(mockChannelSftp);

        ChannelSftp channelSftp = jSchManager.createChannelSftp(mockSession);

        verify(mockChannelSftp).connect();
        assertEquals(mockChannelSftp, channelSftp);
    }

    @Test
    void createChannelSftp_ThrowsJSchException() throws JSchException {
        String channelName = "sftp";

        when(mockSession.openChannel(channelName)).thenThrow(new JSchException());

        assertThrows(CreateChannelSftpException.class, () -> {
           jSchManager.createChannelSftp(mockSession);
        });
    }

    @Test
    void createChannelExec_SuccessfulTest() throws JSchException {
        String channelName = "exec";

        when(mockSession.openChannel(channelName)).thenReturn(mockChannelExec);

        ChannelExec channelExec = jSchManager.createChannelExec(mockSession);

        assertEquals(mockChannelExec, channelExec);
    }

    @Test
    void createChannelExec_ThrowsJSchException() throws JSchException {
        String channelName = "exec";

        when(mockSession.openChannel(channelName)).thenThrow(new JSchException());

        assertThrows(CreateChannelExecException.class, () -> {
            jSchManager.createChannelExec(mockSession);
        });
    }

}
