package live.ioteatime.ruleengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class LocalMidnightDto {
    private LocalDateTime yesterday;
    private LocalDateTime today;
}
