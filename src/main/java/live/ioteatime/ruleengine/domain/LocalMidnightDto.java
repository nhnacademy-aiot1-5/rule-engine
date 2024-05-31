package live.ioteatime.ruleengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class LocalMidnightDto {

    private LocalDateTime yesterday;

    private LocalDateTime today;
}
