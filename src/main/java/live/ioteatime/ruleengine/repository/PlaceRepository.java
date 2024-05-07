package live.ioteatime.ruleengine.repository;

import live.ioteatime.ruleengine.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Integer> {
    List<PlaceEntity> findAllBy();
}
