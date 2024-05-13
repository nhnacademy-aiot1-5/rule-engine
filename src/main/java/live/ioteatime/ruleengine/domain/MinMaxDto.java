package live.ioteatime.ruleengine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class MinMaxDto {
    @JsonProperty("updated_at")
    private LocalDate updatedAt;
    private Double min;
    private Double max;
}
