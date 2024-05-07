package live.ioteatime.ruleengine.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceDto {
    private int placeId;
    private int organizationId;
    private String placeName;
}
