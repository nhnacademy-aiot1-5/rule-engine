package live.ioteatime.ruleengine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class MinMaxDto {

    private int id;

    @JsonProperty("updated_at")
    private LocalDate updatedAt;

    private Double min;

    private Double max;
}
