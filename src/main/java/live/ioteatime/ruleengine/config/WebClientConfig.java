package live.ioteatime.ruleengine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${api.base.uri}")
    private String baseUri;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(baseUri).build();
    }

}
