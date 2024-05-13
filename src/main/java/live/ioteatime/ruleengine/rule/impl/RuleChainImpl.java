package live.ioteatime.ruleengine.rule.impl;

import live.ioteatime.ruleengine.domain.MqttModbusDTO;
import live.ioteatime.ruleengine.rule.Rule;
import live.ioteatime.ruleengine.rule.RuleChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RuleChainImpl implements RuleChain {
    private final List<Rule> rules;
    private final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    @Override
    public void resetThreadLocal() {
        threadLocal.set(0);
    }

    @Override
    public void clearThreadLocal() {
        threadLocal.remove();
        log.info("threadLocal clear");
    }

    @Override
    public void doProcess(MqttModbusDTO mqttModbusDTO) {
        Integer index = threadLocal.get();
        if (index >= rules.size()) {

            return;
        }
        threadLocal.set(index + 1);
        rules.get(index).doProcess(mqttModbusDTO,this);
    }

}
