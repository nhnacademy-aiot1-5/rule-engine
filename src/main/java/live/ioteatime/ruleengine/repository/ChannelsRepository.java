package live.ioteatime.ruleengine.repository;

import live.ioteatime.ruleengine.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelsRepository extends JpaRepository<ChannelEntity, Integer> {

    List<ChannelEntity> findAllBy();
}
