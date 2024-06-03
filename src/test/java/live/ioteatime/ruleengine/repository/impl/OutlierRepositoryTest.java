package live.ioteatime.ruleengine.repository.impl;

import live.ioteatime.ruleengine.domain.MinMaxDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutlierRepositoryTest {

    @InjectMocks
    OutlierRepository outlierRepository;

    Map<String, MinMaxDto> outliers = new HashMap<>();

    @BeforeEach
    void setUp() {
        MinMaxDto minMaxDto1 = new MinMaxDto();
        minMaxDto1.setMin(1d);
        minMaxDto1.setMax(1d);
        minMaxDto1.setId(1);
        MinMaxDto minMaxDto2 = new MinMaxDto();
        minMaxDto2.setMin(1d);
        minMaxDto2.setMax(1d);
        minMaxDto2.setId(1);
        outliers.put("1", minMaxDto1);
        outliers.put("2", minMaxDto2);

        ReflectionTestUtils.setField(outlierRepository, "outliers", outliers);
    }

    @Test
    void clearOutlier() {
        outlierRepository.clearOutlier();

        assertEquals(0, outliers.size());
    }

    @Test
    void getKeys() {
        Set<String> sets = new HashSet<>();
        sets.add("1");
        sets.add("2");

        List<String> keys = Arrays.asList("1", "2");

        List<String> actual = outlierRepository.getKeys();

        assertEquals(keys, actual);
    }

    @Test
    void getOutliers() {
        Map<String, MinMaxDto> outliers1 = outlierRepository.getOutliers();

        assertEquals(outliers, outliers1);
    }

}
