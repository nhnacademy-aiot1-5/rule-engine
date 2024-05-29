package live.ioteatime.ruleengine.repository;

import live.ioteatime.ruleengine.domain.MappingData;
import live.ioteatime.ruleengine.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelsRepository extends JpaRepository<ChannelEntity, Integer> {

    @Query(value = "select ch.channel_name as channelName, ch.address, p.place_name as placeName, t.type, t.value "+
            "FROM channels ch "+
            "join places p on ch.place_id = p.place_id " +
            "join tags t on ch.channel_id = t.channel_id ",nativeQuery = true)
    List<MappingData> loadMappingTable();
}
