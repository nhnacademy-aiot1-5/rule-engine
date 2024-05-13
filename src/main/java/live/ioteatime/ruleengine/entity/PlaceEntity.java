package live.ioteatime.ruleengine.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "places")
public class PlaceEntity {
    @Id
    private int placeId;
    private int organizationId;
    private String placeName;
}
