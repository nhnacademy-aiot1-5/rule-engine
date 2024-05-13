package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.MappingData;
import live.ioteatime.ruleengine.repository.ChannelsRepository;
import live.ioteatime.ruleengine.repository.MappingTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class MappingTableServiceImplTest {
    @Mock
    private MappingTableRepository mappingTableRepository;
    @Mock
    private ChannelsRepository channelsRepository;
    @InjectMocks
    private MappingTableServiceImpl mappingTableService;

    @Test
    void getMappingTable() {
        MappingData mappingData = new MappingData() {
            @Override
            public String getChannel_name() {
                return "channel";
            }

            @Override
            public Integer getAddress() {
                return 0;
            }

            @Override
            public String getPlace_name() {
                return "test";
            }

            @Override
            public String getType() {
                return "test";
            }

            @Override
            public String getValue() {
                return "test";
            }
        };

        List<MappingData> mappingDataList = List.of(mappingData);

        when(channelsRepository.loadMappingTable()).thenReturn(mappingDataList);
        doNothing().when(mappingTableRepository).addValue(mappingDataList);

        mappingTableService.getMappingTable();

        verify(channelsRepository).loadMappingTable();
        verify(mappingTableRepository).addValue(mappingDataList);

    }
}