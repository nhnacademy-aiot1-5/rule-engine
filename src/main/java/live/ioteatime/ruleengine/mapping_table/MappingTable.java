package live.ioteatime.ruleengine.mapping_table;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 모드버스 통신을 매핑 하기 위한 정보들을 담는 클래스 입니다.
 */
@Component
@Getter
@Setter
public class MappingTable {
    Map<Integer, String> place = new HashMap<>();
    Map<Integer, String> types = new HashMap<>();
    Map<Integer, String> phase = new HashMap<>();
    Map<Integer, String> description = new HashMap<>();
}
