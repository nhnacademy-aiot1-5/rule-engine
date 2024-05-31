package live.ioteatime.ruleengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Outlier {

    LIGHT("do_1","light"),
    AC("do_2","ac");

    private final String id;
    private final String lowercase;
}
