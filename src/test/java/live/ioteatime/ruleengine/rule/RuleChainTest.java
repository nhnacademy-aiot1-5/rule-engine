package live.ioteatime.ruleengine.rule;

import live.ioteatime.ruleengine.domain.MqttData;
import live.ioteatime.ruleengine.rule.impl.RuleChainImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
class RuleChainTest {

    @Mock
    private Rule rule1;
    @Mock
    private Rule rule2;
    @Mock
    private Rule rule3;

    @InjectMocks
    private RuleChainImpl ruleChain;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ruleChain = new RuleChainImpl(Arrays.asList(rule1, rule2, rule3));
    }

    @Test
    void doProcess() {
        MqttData data = new MqttData();
        data.setTopic("adssad");


        ruleChain.resetThreadLocal();
        ruleChain.doProcess(data);

        verify(rule1).doProcess(any(MqttData.class), any(RuleChain.class));
        verify(rule2).doProcess(any(MqttData.class), any(RuleChain.class));
        verify(rule3).doProcess(any(MqttData.class), any(RuleChain.class));


    }
}