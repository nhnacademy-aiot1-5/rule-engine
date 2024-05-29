package live.ioteatime.ruleengine.repository.impl;

import live.ioteatime.ruleengine.domain.MinMaxDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutlierRepositoryTest {
    @Mock
    Map<String, MinMaxDto> outliers = new HashMap<>();
    @InjectMocks
    OutlierRepository outlierRepository;

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

        when(outliers.keySet()).thenReturn(sets);

        List<String> actual = outlierRepository.getKeys();

        assertEquals(keys, actual);
    }

    @Test
    void getOutliers() {
        Map<String, MinMaxDto> outliers1 = outlierRepository.getOutliers();

        assertEquals(outliers, outliers1);
    }

}
