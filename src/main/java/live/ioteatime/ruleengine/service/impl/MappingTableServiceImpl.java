package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.MappingData;
import live.ioteatime.ruleengine.repository.ChannelsRepository;
import live.ioteatime.ruleengine.repository.MappingTableRepository;
import live.ioteatime.ruleengine.service.MappingTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MappingTableServiceImpl implements MappingTableService {

    private static final String LOGGING_GET_MAPPING = "getMappingTable {}";

    @Value("${mapping.flag}")
    private boolean mappingFlag;

    private final ChannelsRepository channelsRepository;
    private final MappingTableRepository mappingTableRepository;

    @PostConstruct
    private void initQuery() {
        if (mappingFlag) {
            this.getMappingTable();
        }
    }

    @Override
    public void getMappingTable() {
        List<MappingData> mappingDataDto = channelsRepository.loadMappingTable();

        mappingTableRepository.addValue(mappingDataDto);
        log.debug(LOGGING_GET_MAPPING, mappingTableRepository.getTables());
    }

    @Override
    public Map<String, String> getTags(int address) {
        return mappingTableRepository.getTags(address);
    }
}
