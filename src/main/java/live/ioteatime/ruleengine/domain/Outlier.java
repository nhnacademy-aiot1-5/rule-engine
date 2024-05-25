package live.ioteatime.ruleengine.domain;

import lombok.Getter;

@Getter
public enum Outlier {
    LIGHT("do_1","light"),
    AC("do_2","ac");

    private final String id;
    private final String lowercase;

    Outlier(String id,String lowercase) {
        this.id = id;
        this.lowercase = lowercase;
    }

}
