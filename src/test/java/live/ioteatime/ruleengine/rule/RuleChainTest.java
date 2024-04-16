package live.ioteatime.ruleengine.rule;

import live.ioteatime.ruleengine.domain.MqttData;
import live.ioteatime.ruleengine.rule.impl.RuleChainImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.*;

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

        Answer answer = i -> {
            MqttData argument1 = i.getArgument(0, MqttData.class);
            RuleChain argument = i.getArgument(1, RuleChain.class);
            argument.doProcess(argument1);
            return null;
        };

        doAnswer(answer).when(rule1).doProcess(any(), any());
        doAnswer(answer).when(rule2).doProcess(any(), any());
        doAnswer(answer).when(rule3).doProcess(any(), any());

        ruleChain.resetThreadLocal();
        ruleChain.doProcess(data);

        verify(rule1).doProcess(any(MqttData.class), any(RuleChain.class));
        verify(rule2).doProcess(any(MqttData.class), any(RuleChain.class));
        verify(rule3).doProcess(any(MqttData.class), any(RuleChain.class));

        List<Rule> rules = (List<Rule>) getField(ruleChain, "rules");
        Assertions.assertTrue(rules.size() == 3);
    }
}