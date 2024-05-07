package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.ChannelDto;
import live.ioteatime.ruleengine.domain.PlaceDto;
import live.ioteatime.ruleengine.entity.ChannelEntity;
import live.ioteatime.ruleengine.entity.PlaceEntity;
import live.ioteatime.ruleengine.mapping_table.MappingTable;
import live.ioteatime.ruleengine.repository.ChannelsRepository;
import live.ioteatime.ruleengine.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MappingTableServiceImplTest {

    @Mock
    private ChannelsRepository channelsRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private MappingTable mappingTable;

    @InjectMocks
    private MappingTableServiceImpl mappingTableService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void getMappingTable() {
        ChannelDto channelDto1 = ChannelDto.builder().channelId(1).channelName("asd").address(8000).quantity(4).placeId(1).sensorId(1).functionCode(1).build();
        ChannelDto channelDto2 = ChannelDto.builder().channelId(1).channelName("asd").address(8000).quantity(4).placeId(1).sensorId(1).functionCode(1).build();
        PlaceDto placeDto1 = PlaceDto.builder().placeId(1).placeName("asd").organizationId(1).placeId(1).build();
        PlaceDto placeDto2 = PlaceDto.builder().placeId(1).placeName("asd").organizationId(1).placeId(1).build();
        List<ChannelDto> channelEntityList = List.of(channelDto1,channelDto2);
        List<PlaceDto> placeDtoList = List.of(placeDto1,placeDto2);
        Map<Integer, String> type = mock(Map.class);
        Map<Integer, String> phase = mock(Map.class);
        Map<Integer, String> des = mock(Map.class);
        Map<Integer, String> place = mock(Map.class);

        when(mappingTable.getTypes()).thenReturn(type);
        when(mappingTable.getPhase()).thenReturn(phase);
        when(mappingTable.getDescription()).thenReturn(des);
        when(mappingTable.getPlace()).thenReturn(place);

        mappingTableService.setTable(channelEntityList,placeDtoList);

        verify(type,times(channelEntityList.size())).put(anyInt(), anyString());
        verify(phase,atLeastOnce()).put(anyInt(), anyString());
        verify(des,atLeastOnce()).put(anyInt(), anyString());

    }


    @Test
    void getChannelsTest() {
        ChannelEntity channel1 = new ChannelEntity();
        ReflectionTestUtils.setField(channel1, "channelId", 1);
        ReflectionTestUtils.setField(channel1, "sensorId", 1);
        ReflectionTestUtils.setField(channel1, "placeId", 1);
        ReflectionTestUtils.setField(channel1, "channelName", "main");
        ReflectionTestUtils.setField(channel1, "address", 1);
        ReflectionTestUtils.setField(channel1, "quantity", 2);
        ReflectionTestUtils.setField(channel1, "functionCode", 1);
        ChannelEntity channel2 = new ChannelEntity();
        ReflectionTestUtils.setField(channel2, "channelId", 1);
        ReflectionTestUtils.setField(channel2, "sensorId", 1);
        ReflectionTestUtils.setField(channel2, "placeId", 1);
        ReflectionTestUtils.setField(channel2, "channelName", "main");
        ReflectionTestUtils.setField(channel2, "address", 1);
        ReflectionTestUtils.setField(channel2, "quantity", 2);
        ReflectionTestUtils.setField(channel2, "functionCode", 1);
        List<ChannelEntity> channelEntities = List.of(channel1,channel2);

        when(channelsRepository.findAllBy()).thenReturn(channelEntities);

        List<ChannelDto> result = mappingTableService.getChannels();

        assertEquals(channelEntities.size(), result.size());
        assertEquals(1,result.get(0).getChannelId());
        assertEquals("main",result.get(0).getChannelName());
    }

    @Test
    void getPlacesTest() {
        PlaceEntity place1 = new PlaceEntity();
        ReflectionTestUtils.setField(place1,"placeId",1);
        ReflectionTestUtils.setField(place1,"organizationId",1);
        ReflectionTestUtils.setField(place1,"placeName","main");
        PlaceEntity place2 = new PlaceEntity();
        ReflectionTestUtils.setField(place2,"placeId",2);
        ReflectionTestUtils.setField(place2,"organizationId",2);
        ReflectionTestUtils.setField(place2,"placeName","aa");
        List<PlaceEntity> placeEntities = List.of(place1,place2);

        when(placeRepository.findAllBy()).thenReturn(placeEntities);

        List<PlaceDto> result = mappingTableService.getPlaces();

        assertEquals(placeEntities.size(), result.size());
        assertEquals(1,result.get(0).getPlaceId());
    }
}