package live.ioteatime.ruleengine.rule;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.rule.impl.RuleChainImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.getField;

@Slf4j
class RuleChainTest {
    @Mock
    private Rule rule1;
    @Mock
    private Rule rule2;
    @Mock
    private Rule rule3;

    private RuleChainImpl ruleChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ruleChain = new RuleChainImpl(Arrays.asList(rule1, rule2, rule3));
    }

    @Test
    void doProcess() throws Exception {
        MqttModbusDTO data = getMqttData();
        data.setId("adssad");

        Answer answer = i -> {
            MqttModbusDTO argument1 = i.getArgument(0, MqttModbusDTO.class);
            RuleChain argument = i.getArgument(1, RuleChain.class);
            argument.doProcess(argument1);
            return null;
        };

        doAnswer(answer).when(rule1).doProcess(any(), any());
        doAnswer(answer).when(rule2).doProcess(any(), any());
        doAnswer(answer).when(rule3).doProcess(any(), any());

        ruleChain.resetThreadLocal();
        ruleChain.doProcess(data);

        verify(rule1).doProcess(any(MqttModbusDTO.class), any(RuleChain.class));
        verify(rule2).doProcess(any(MqttModbusDTO.class), any(RuleChain.class));
        verify(rule3).doProcess(any(MqttModbusDTO.class), any(RuleChain.class));

        List<Rule> rules = (List<Rule>) getField(ruleChain, "rules");
        Assertions.assertTrue(rules.size() == 3);
    }

    private MqttModbusDTO getMqttData() throws Exception {
        Constructor<?> constructor = Class.forName("live.ioteatime.ruleengine.domain.MqttModbusDTO").getDeclaredConstructor();
        constructor.setAccessible(true);

        return (MqttModbusDTO) constructor.newInstance();
    }

}
