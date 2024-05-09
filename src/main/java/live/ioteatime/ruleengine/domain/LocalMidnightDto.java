package live.ioteatime.ruleengine.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class LocalMidnightDto {
    private LocalDateTime yesterday;
    private LocalDateTime today;
}
