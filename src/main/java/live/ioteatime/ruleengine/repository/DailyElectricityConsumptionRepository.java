package live.ioteatime.ruleengine.repository;

import live.ioteatime.ruleengine.domain.DailyPower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyElectricityConsumptionRepository extends JpaRepository<DailyPower, Integer> {

}
