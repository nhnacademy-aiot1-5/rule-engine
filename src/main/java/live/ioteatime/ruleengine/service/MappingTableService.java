package live.ioteatime.ruleengine.service;

import java.util.Map;

public interface MappingTableService {

    void getMappingTable();

    Map<String, String> getTags(int address);
}

