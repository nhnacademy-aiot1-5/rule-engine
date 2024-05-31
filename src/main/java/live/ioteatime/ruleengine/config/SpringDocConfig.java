package live.ioteatime.ruleengine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("RuleEngine API 문서")
                .version("v1.0")
                .description("RuleEngine 서비스 문서");

        return new OpenAPI()
                .info(info);
    }

}
