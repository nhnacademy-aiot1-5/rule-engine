package live.ioteatime.ruleengine.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "controller_status")
@AllArgsConstructor
@NoArgsConstructor
public class ControllerStatusEntity {
    @Id
    private String controllerId;
    @Setter
    private int status;
}
