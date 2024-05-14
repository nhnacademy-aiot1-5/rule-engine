package live.ioteatime.ruleengine.repository;

import live.ioteatime.ruleengine.domain.MappingData;
import live.ioteatime.ruleengine.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelsRepository extends JpaRepository<ChannelEntity, Integer> {

    List<ChannelEntity> findAllBy();

    @Query(value = "SELECT ch.channel_id " +
            "FROM channels ch " +
            "JOIN places p ON ch.place_id = p.place_id " +
            "JOIN tags t ON ch.channel_id = t.channel_id " +
            "JOIN tags t2 ON ch.channel_id = t2.channel_id " +
            "WHERE ch.channel_name = :type " +
            "AND t.type = 'phase' " +
            "AND t.value = :phase " +
            "AND t2.type = 'description' " +
            "AND t2.value = :description " +
            "AND p.place_name = :place", nativeQuery = true)
    Integer findChannelIdByTags(@Param("type") String type,
                                @Param("phase") String phase,
                                @Param("description") String description,
                                @Param("place") String place);

    @Query(value = "select ch.channel_name as channelName, ch.address, p.place_name as placeName, t.type, t.value "+
            "FROM channels ch "+
            "join places p on ch.place_id = p.place_id " +
            "join tags t on ch.channel_id = t.channel_id ",nativeQuery = true)
    List<MappingData> loadMappingTable();
}
