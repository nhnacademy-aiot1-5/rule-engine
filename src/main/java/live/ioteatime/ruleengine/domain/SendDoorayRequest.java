package live.ioteatime.ruleengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SendDoorayRequest {
    private String botName;
    private String text;
}
