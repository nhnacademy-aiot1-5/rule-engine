package live.ioteatime.ruleengine.repository.impl;

import live.ioteatime.ruleengine.domain.MappingData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MappingTableRepositoryImplTest {
    MappingTableRepositoryImpl repository = new MappingTableRepositoryImpl();
    MappingData test1 = createMappingData("test", 1, "place", "typeTest", "value");
    MappingData test2 = createMappingData("test1", 2, "place2", "typeTest2", "value2");
    MappingData test3 = createMappingData("test2", 3, "place3", "typeTest3", "value3");

    @BeforeEach
    void setUp() {
        List<MappingData> mappingData = new ArrayList<>(List.of(test1, test2, test3));
        repository.addValue(mappingData);
    }

    @Test
    void getTables() {
        Set<Map.Entry<Integer, Map<String, String>>> tables = repository.getTables();

        assertNotNull(tables);
        assertFalse(tables.isEmpty());
    }

    @Test
    void getTags() {
        Map<String, String> tags = repository.getTags(1);

        assertNotNull(tags);
        assertEquals(test1.getPlaceName(), tags.get("place"));
        assertEquals(test1.getChannelName(), tags.get("type"));
        assertEquals(test1.getValue(), tags.get("typeTest"));
    }

    @Test
    void addValue() {
        MappingData newMappingData = createMappingData("test4", 4, "place4", "typeTest4", "value4");
        repository.addValue(Collections.singletonList(newMappingData));

        Map<String, String> tags = repository.getTags(newMappingData.getAddress());

        assertNotNull(tags);
        assertEquals("value4", tags.get("typeTest4"));
    }

    private MappingData createMappingData(String name, Integer address, String place, String type, String value) {
        return new MappingData() {
            @Override
            public String getChannelName() {
                return name;
            }

            @Override
            public Integer getAddress() {
                return address;
            }

            @Override
            public String getPlaceName() {
                return place;
            }

            @Override
            public String getType() {
                return type;
            }

            @Override
            public String getValue() {
                return value;
            }
        };
    }

}