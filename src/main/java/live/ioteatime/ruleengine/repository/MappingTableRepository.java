package live.ioteatime.ruleengine.repository;

import live.ioteatime.ruleengine.domain.MappingData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class MappingTableRepository {
    private final Map<Integer, Map<String, String>> mappingTable = new HashMap<>();

    public Set<Map.Entry<Integer, Map<String, String>>> getTables() {
        return mappingTable.entrySet();
    }

    public Map<String, String> getTags(int address) {
        return mappingTable.get(address);
    }

    public void addValue(List<MappingData> table) {
        table.forEach(mappingData -> {
            if (mappingTable.containsKey(mappingData.getAddress())) {
                Map<String, String> map = mappingTable.get(mappingData.getAddress());
                map.put(mappingData.getType(), mappingData.getValue());

                return;
            }

            Map<String, String> tag = new HashMap<>(Map.of("type", mappingData.getChannel_name(), "place", mappingData.getPlace_name(), mappingData.getType(), mappingData.getValue()));
            mappingTable.put(mappingData.getAddress(),tag);
        });
    }

}
