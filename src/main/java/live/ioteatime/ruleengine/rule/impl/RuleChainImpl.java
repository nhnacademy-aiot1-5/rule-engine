package live.ioteatime.ruleengine.rule.impl;

import live.ioteatime.ruleengine.domain.MqttData;
import live.ioteatime.ruleengine.rule.Rule;
import live.ioteatime.ruleengine.rule.RuleChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RuleChainImpl implements RuleChain {
    private final List<Rule> rules;
    private final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
    @Override
    public void resetThreadLocal() {
        threadLocal.set(0);

    }

    @Override
    public void doProcess(MqttData mqttData) {
        Integer index = threadLocal.get();
        if (index >= rules.size()) {
            return;
        }
        threadLocal.set(index + 1);
        rules.get(index).doProcess(mqttData,this);
    }
}
