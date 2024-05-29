package live.ioteatime.ruleengine.scheduler;

import live.ioteatime.ruleengine.domain.OutlierDto;
import live.ioteatime.ruleengine.handler.MqttDataHandlerContext;
import live.ioteatime.ruleengine.service.OutlierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {
    @Mock
    MqttDataHandlerContext mqttDataHandlerContext;
    @Mock
    OutlierService outlierService;
    @InjectMocks
    Scheduler scheduler;

    @Test
    void outlierUpdater() {
        ReflectionTestUtils.setField(scheduler, "cronFlag", true);
        List<OutlierDto> outlierDtos = new ArrayList<>(List.of(new OutlierDto()));

        doNothing().when(mqttDataHandlerContext).pauseAll();
        doNothing().when(mqttDataHandlerContext).restartAll();
        when(outlierService.getOutlier(anyString())).thenReturn(outlierDtos);

        scheduler.outlierUpdater();

        verify(mqttDataHandlerContext).pauseAll();
        verify(outlierService).getOutlier(anyString());
        verify(outlierService).matchTime(any(), any());
        verify(mqttDataHandlerContext).restartAll();
    }

}
