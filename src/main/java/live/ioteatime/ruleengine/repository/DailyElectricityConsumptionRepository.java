package live.ioteatime.ruleengine.repository;

import live.ioteatime.ruleengine.entity.DailyPowerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyElectricityConsumptionRepository extends JpaRepository<DailyPowerEntity, Integer> {

}
