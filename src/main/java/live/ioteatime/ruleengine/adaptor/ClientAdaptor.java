package live.ioteatime.ruleengine.adaptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class ClientAdaptor {
    private final WebClient webClient;

    public void sendPostRequest(String url, Object body) {
        webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), body.getClass())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info("post response: {}", response),
                        error -> log.error("send post request error", error)
                );
    }

    public void sendGetRequest(String url) {
        webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info("get response: {}", response),
                        error -> log.error("send get request error", error)
                );
    }

}
