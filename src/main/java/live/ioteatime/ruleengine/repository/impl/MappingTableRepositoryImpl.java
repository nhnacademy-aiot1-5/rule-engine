package live.ioteatime.ruleengine.repository.impl;

import live.ioteatime.ruleengine.domain.MappingData;
import live.ioteatime.ruleengine.exception.MappingTableIndexNotFoundException;
import live.ioteatime.ruleengine.repository.MappingTableRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class MappingTableRepositoryImpl implements MappingTableRepository {

    private static final String LOGGING_MAPPING = "Mapping table index does not exist";

    private final Map<Integer, Map<String, String>> mappingTable = new HashMap<>();

    public Set<Map.Entry<Integer, Map<String, String>>> getTables() {
        return mappingTable.entrySet();
    }

    public Map<String, String> getTags(int address) {
        Map<String, String> tags = mappingTable.get(address);

        if (tags == null) {
            throw new MappingTableIndexNotFoundException(LOGGING_MAPPING);
        }

        return tags;
    }

    public void addValue(List<MappingData> table) {
        table.forEach(mappingData -> {
            Map<String, String> tag = new HashMap<>(Map.of("type", mappingData.getChannelName(), "place", mappingData.getPlaceName(), mappingData.getType(), mappingData.getValue()));
            mappingTable.put(mappingData.getAddress(), tag);
        });
    }
}
