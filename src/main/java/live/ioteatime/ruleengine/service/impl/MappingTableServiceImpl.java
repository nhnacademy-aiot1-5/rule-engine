package live.ioteatime.ruleengine.service.impl;

import live.ioteatime.ruleengine.domain.ChannelDto;
import live.ioteatime.ruleengine.domain.PlaceDto;
import live.ioteatime.ruleengine.entity.ChannelEntity;
import live.ioteatime.ruleengine.entity.PlaceEntity;
import live.ioteatime.ruleengine.mapping_table.MappingTable;
import live.ioteatime.ruleengine.repository.ChannelsRepository;
import live.ioteatime.ruleengine.repository.PlaceRepository;
import live.ioteatime.ruleengine.service.MappingTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MappingTableServiceImpl implements MappingTableService {
    private final ChannelsRepository channelsRepository;
    private final PlaceRepository placeRepository;
    private final MappingTable mappingTable;

    public void setTable(List<ChannelDto> channels, List<PlaceDto> places) {
        Map<Integer, String> types = mappingTable.getTypes();

        for (ChannelDto channel : channels) {
            types.put(channel.getAddress(), channel.getChannelName());
            putPhase(channel);
            putDescription(channel);
        }
        mappingPlace(places);
        mappingTable.setTypes(types);
    }

    @Override
    public List<ChannelDto> getChannels() {
        List<ChannelEntity> allChannel = channelsRepository.findAllBy();
        List<ChannelDto> channels = new ArrayList<>();

        for (ChannelEntity channel : allChannel) {
            ChannelDto channelDto = ChannelDto.builder().channelId(channel.getChannelId())
                    .sensorId(channel.getSensorId())
                    .placeId(channel.getPlaceId())
                    .channelName(channel.getChannelName())
                    .address(channel.getAddress())
                    .quantity(channel.getQuantity())
                    .functionCode(channel.getFunctionCode())
                    .build();

            channels.add(channelDto);
        }
        log.info("{} have been updated ", channels.size());

        return channels;
    }

    @Override
    public List<PlaceDto> getPlaces() {
        List<PlaceEntity> allPlace = placeRepository.findAllBy();
        List<PlaceDto> places = new ArrayList<>();

        for (PlaceEntity place : allPlace) {
            PlaceDto placeDto = PlaceDto.builder()
                    .placeId(place.getPlaceId())
                    .organizationId(place.getOrganizationId())
                    .placeName(place.getPlaceName())
                    .build();
            places.add(placeDto);
        }
        log.info("getPlaces size: {}", places.size());

        return places;
    }

    private void putDescription(ChannelDto channel) {
        Map<Integer, String> description = mappingTable.getDescription();

        for (int i = 0; i <= channel.getQuantity(); i++) {

            if (i >=0  && i <2) description.put(channel.getAddress() + i, "sum");

            if (i > 1 && i <4) description.put(channel.getAddress() + i, "this_month");

            if (i > 3 && i <7) description.put(channel.getAddress() + i, "last_month");
        }
        mappingTable.setDescription(description);
    }

    private void putPhase(ChannelDto channel) {
        Map<Integer, String> phase = mappingTable.getPhase();

        for (int i = 0; i <= channel.getQuantity(); i++) {

            if (i >= 0 && i < 6) phase.put(channel.getAddress() + i, "kwh");

            if (i > 5 && i < 12) phase.put(channel.getAddress() + i, "kvarh");

            if (i > 11 && i < 18) phase.put(channel.getAddress() + i, "kvah");
        }
        mappingTable.setPhase(phase);
    }

    private void mappingPlace(List<PlaceDto> places) {
        Map<Integer, String> place = mappingTable.getPlace();

        for (PlaceDto placeDto : places) {
            place.put(placeDto.getPlaceId(), placeDto.getPlaceName());
        }
        mappingTable.setPlace(place);
    }

}
