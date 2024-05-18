package live.ioteatime.ruleengine.manager;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import live.ioteatime.ruleengine.exception.CreateJSchSessionException;
import live.ioteatime.ruleengine.properties.JschProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
    public void setUp() {
        ReflectionTestUtils.setField(jSchManager, "jSch", jsch);
    }

    @Test
    void createSession() throws JSchException, CreateJSchSessionException {
        when(jschProperties.getPrivateKey()).thenReturn("private_key_path");
        when(jschProperties.getUser()).thenReturn("user");
        when(jschProperties.getHost()).thenReturn("host");
        when(jsch.getSession(jschProperties.getUser(), jschProperties.getHost(), 22)).thenReturn(mockSession);

        Session session = jSchManager.createSession();

        verify(mockSession).connect();
        assertEquals(mockSession, session);
    }

    @Test
    void createChannelSftp() throws JSchException {
        String channelName = "sftp";

        when(mockSession.openChannel(channelName)).thenReturn(mockChannelSftp);

        ChannelSftp channelSftp = jSchManager.createChannelSftp(mockSession);

        verify(mockChannelSftp).connect();
        assertEquals(mockChannelSftp, channelSftp);
    }

    @Test
    void createChannelExec() throws JSchException {
        String channelName = "exec";

        when(mockSession.openChannel(channelName)).thenReturn(mockChannelExec);

        ChannelExec channelExec = jSchManager.createChannelExec(mockSession);

        assertEquals(mockChannelExec, channelExec);
    }

}
