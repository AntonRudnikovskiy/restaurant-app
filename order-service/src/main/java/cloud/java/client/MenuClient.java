package cloud.java.client;

import cloud.java.config.OrderServiceProps;
import cloud.java.dto.GetMenuInfoRequest;
import cloud.java.dto.GetMenuInfoResponse;
import cloud.java.exception.OrderServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class MenuClient {
    private final WebClient webClient;
    private final OrderServiceProps props;

    public Mono<GetMenuInfoResponse> getMenuInfo(GetMenuInfoRequest request) {
        return webClient
                .post()
                .uri(props.getMenuInfoPath())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new OrderServiceException("Menu Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE)))
                .bodyToMono(GetMenuInfoResponse.class)
                .timeout(props.getDefaultTimeout())
                .retryWhen(
                        Retry.backoff(props.getRetryCount(), props.getRetryBackoff())
                                .jitter(props.getRetryJitter())
                                .filter(t -> t instanceof OrderServiceException || t instanceof TimeoutException)
                                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> {
                                    String msg = "Failed to fetch info from Menu Service after max retry attempts";
                                    throw new OrderServiceException(msg, HttpStatus.SERVICE_UNAVAILABLE);
                                }))
                );
    }
}
