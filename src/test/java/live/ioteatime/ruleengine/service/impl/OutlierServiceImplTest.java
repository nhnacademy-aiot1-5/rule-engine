package live.ioteatime.ruleengine.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.ioteatime.ruleengine.domain.LocalDateTimeDto;
import live.ioteatime.ruleengine.domain.MinMaxDto;
import live.ioteatime.ruleengine.domain.OutlierDto;
import live.ioteatime.ruleengine.repository.impl.OutlierRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
class OutlierServiceImplTest {
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @Mock
    ValueOperations<String, Object> valueOperations;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    OutlierServiceImpl service;

    @Test
    void getOutlierTest() throws Exception {
        String key = "key";
        String redisResponse = "{\"field1\":\"value1\"}";

        MinMaxDto minMaxDto = new MinMaxDto();
        minMaxDto.setMin(1d);
        minMaxDto.setMax(2d);

        Map<Integer, MinMaxDto> hour = new HashMap<>();
        hour.put(1, minMaxDto);

        Constructor<OutlierDto> constructor = OutlierDto.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        OutlierDto outlierDto = constructor.newInstance();
        outlierDto.setHour(hour);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(valueOperations.get(key)).thenReturn(redisResponse);
        lenient().when(objectMapper.readValue(redisResponse, OutlierDto.class)).thenReturn(outlierDto);

        OutlierDto outlier = service.getOutlier(key);

        assertEquals(outlierDto, outlier);
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(key);
        verify(objectMapper).readValue(redisResponse, OutlierDto.class);
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
    void matchTimeTest() throws Exception {
        int time = Integer.parseInt(LocalTime.now().toString().split(":")[0]);

        MinMaxDto minMaxDto = new MinMaxDto();
        minMaxDto.setMin(1d);
        minMaxDto.setMax(2d);
        minMaxDto.setUpdatedAt(LocalDate.now());

        Map<Integer, MinMaxDto> hour = new HashMap<>();
        hour.put(time, minMaxDto);

        Constructor<OutlierDto> constructor = OutlierDto.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        OutlierDto outlierDto = constructor.newInstance();
        outlierDto.setHour(hour);

        LocalDate localDate = LocalDate.now();
        LocalDateTimeDto localDateTimeDto = new LocalDateTimeDto(localDate, time);

        MinMaxDto result = service.matchTime(outlierDto, localDateTimeDto).orElse(null);

        assertNotNull(result);
        assertEquals(hour.get(time).getUpdatedAt(),result.getUpdatedAt());
    }

    @Test
    void updateOutlierTest() {
        OutlierRepository outlierRepository1 = new OutlierRepository();
        ReflectionTestUtils.setField(service, "outlierRepository", outlierRepository1);

        MinMaxDto minMaxDto = new MinMaxDto();
        minMaxDto.setMin(1d);
        minMaxDto.setMax(2d);
        minMaxDto.setUpdatedAt(LocalDate.now());

        service.updateOutlier(minMaxDto);

        assertEquals(minMaxDto.getMin(), outlierRepository1.getMin());
        assertEquals(minMaxDto.getMax(), outlierRepository1.getMax());
    }
}