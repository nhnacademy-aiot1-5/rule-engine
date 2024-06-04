package live.ioteatime.ruleengine.adaptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class ClientAdaptor {

    private static final String LOGGING_POST_RESPONSE = "post response: {}";
    private static final String LOGGING_POST_ERROR = "send post request error: {}";
    private static final String LOGGING_FRONT_RESPONSE = "front post response: {}";
    private static final String LOGGING_FRONT_ERROR = "send post request error: {}";
    private static final String LOGGING_GET_RESPONSE = "get response: {}";
    private static final String LOGGING_GET_ERROR = "send get request error {}";

    @Value("${front.header.name}")
    private String headerName;

    @Value("${front.header.value}")
    private String headerValue;

    private final WebClient webClient;

    public void sendPostRequest(String url, Object body) {
        webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), body.getClass())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info(LOGGING_POST_RESPONSE, response),
                        error -> log.error(LOGGING_POST_ERROR, error.getMessage())
                );
    }

    public void sendPostRequestFront(String url, Object body) {
        webClient.post()
                .uri(url)
                .header(headerName, headerValue)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), body.getClass())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info(LOGGING_FRONT_RESPONSE, response),
                        error -> log.error(LOGGING_FRONT_ERROR, error.getMessage())
                );
    }

    public void sendGetRequest(String url) {
        webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info(LOGGING_GET_RESPONSE, response),
                        error -> log.error(LOGGING_GET_ERROR, error.getMessage())
                );
    }
}
