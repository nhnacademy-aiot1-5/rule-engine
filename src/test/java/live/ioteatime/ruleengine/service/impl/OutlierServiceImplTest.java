package live.ioteatime.ruleengine.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.OutlierDto;
import live.ioteatime.ruleengine.repository.impl.OutlierRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class OutlierServiceImplTest {
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @Mock
    ValueOperations<String, Object> valueOperations;
    @Mock
    OutlierRepository outlierRepository;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    OutlierServiceImpl service;
    ObjectMapper mapper = new ObjectMapper();

    String key = "test";
    String json = "[{\"place\": \"class_a\", \"values\": [{\"id\": 0, \"min\": -1601.77, \"max\": 3355.83, \"updated_at\": \"2024-05-23\"}, {\"id\": 1, \"min\": -1379.65, \"max\": 2597.18, \"updated_at\": \"2024-05-23\"}, {\"id\": 2, \"min\": -952.17, \"max\": 1970.77, \"updated_at\": \"2024-05-23\"}]}, {\"place\": \"office\", \"values\": [{\"id\": 0, \"min\": -1379.55, \"max\": 4610.01, \"updated_at\": \"2024-05-23\"}, {\"id\": 1, \"min\": -575.72, \"max\": 3000.02, \"updated_at\": \"2024-05-23\"}, {\"id\": 2, \"min\": -492.42, \"max\": 2873.64, \"updated_at\": \"2024-05-23\"}]}]";
    List<OutlierDto> outlierDto;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ReflectionTestUtils.setField(service, "objectMapper", mapper);
        ReflectionTestUtils.setField(service, "outlierRepository", outlierRepository);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(valueOperations.get(key)).thenReturn(json);
        lenient().when(objectMapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, OutlierDto.class))).thenReturn(outlierDto);

        outlierDto = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, OutlierDto.class));
    }

    @Test
    void getOutlierTest() {

        List<OutlierDto> outlier = service.getOutlier(key);

        assertEquals(outlierDto.size(), outlier.size());
    }

    @Test
    void localDateTimeTest() {
        int time = Integer.parseInt(LocalTime.now().toString().split(":")[0]);
        LocalDate localDate = LocalDate.now();
        LocalDateTimeDto localDateTimeDto = new LocalDateTimeDto(localDate, time);

        LocalDateTimeDto localDateTimeDto1 = service.localDateTime();

        assertEquals(localDateTimeDto.getDate(), localDateTimeDto1.getDate());
        assertEquals(localDateTimeDto.getTime(), localDateTimeDto1.getTime());
    }

    @Test
    void matchTimeTest() {
        int time = Integer.parseInt(LocalTime.now().toString().split(":")[0]);

        LocalDate localDate = LocalDate.now();
        LocalDateTimeDto localDateTimeDto = new LocalDateTimeDto(localDate, time);

        MinMaxDto minMaxDto = new MinMaxDto();
        minMaxDto.setMin(1D);
        minMaxDto.setMin(-1D);

        OutlierDto outlierDto1 = new OutlierDto();
        outlierDto1.setPlace("class");
        outlierDto1.setValues(List.of(minMaxDto));

        Map<String, MinMaxDto> minMaxDtoMap = new HashMap<>();
        minMaxDtoMap.put("min", minMaxDto);
        List<OutlierDto> outlierDtos = List.of(outlierDto1);

        when(outlierRepository.getOutliers()).thenReturn(minMaxDtoMap);

        service.matchTime(outlierDtos, localDateTimeDto);

        verify(outlierRepository).clearOutlier();
        verify(outlierRepository, times(outlierDtos.size())).getOutliers();
    }

}
