package live.ioteatime.ruleengine.repository;

import live.ioteatime.ruleengine.entity.ControllerStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ControllerStatusRepository extends JpaRepository<ControllerStatusEntity, Integer> {

    ControllerStatusEntity findByControllerId(String controlId);
}
