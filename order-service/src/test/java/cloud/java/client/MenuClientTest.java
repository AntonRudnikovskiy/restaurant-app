package cloud.java.client;

import cloud.java.TestDataProvider;
import cloud.java.config.OrderServiceProps;
import cloud.java.dto.GetMenuInfoRequest;
import cloud.java.dto.GetMenuInfoResponse;
import cloud.java.dto.MenuInfo;
import cloud.java.exception.OrderServiceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static cloud.java.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MenuClientTest {
    private final OrderServiceProps props = new OrderServiceProps(
            "http://localhost:9091",
            "/v1/menu-item/menu-info",
            DEFAULT_TIMEOUT,
            RETRY_BACKOFF,
            RETRY_COUNT,
            RETRY_JITTER
    );

    private MenuClient menuClient;
    private MockWebServer mockWebServer;

    @BeforeEach
    void setServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").url().toString())
                .build();
        menuClient = new MenuClient(webClient, props);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getMenuInfo_returnsInfo_whenRetriesSucceed() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value()));
        mockWebServer.enqueue(TestDataProvider.partialSuccessResponse().setBodyDelay(DELAY_MILLIS, TimeUnit.MILLISECONDS));
        mockWebServer.enqueue(TestDataProvider.partialSuccessResponse());

        GetMenuInfoRequest infoRequest = new GetMenuInfoRequest(Set.of("One", "Two", "Three"));
        Mono<GetMenuInfoResponse> response = menuClient.getMenuInfo(infoRequest);
        assertResponseCorrect(response);
        verifyNumberOfPostRequests(3);
    }

    @Test
    void getMenuInfo_returnsInfo_whenAllIsOk() throws Exception {
        mockWebServer.enqueue(TestDataProvider.partialSuccessResponse());
        var request = new GetMenuInfoRequest(Set.of("One", "Two", "Three"));
        Mono<GetMenuInfoResponse> response = menuClient.getMenuInfo(request);
        assertResponseCorrect(response);
        verifyNumberOfPostRequests(1);
    }

    @Test
    void getMenuInfo_returnsInfo_whenRetriesFailed() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value()));

        GetMenuInfoRequest infoRequest = new GetMenuInfoRequest(Set.of("One", "Two", "Three"));
        Mono<GetMenuInfoResponse> menuInfo = menuClient.getMenuInfo(infoRequest);
        StepVerifier.create(menuInfo)
                .expectError(OrderServiceException.class)
                .verify();
    }

    @Test
    void getMenuInfo_returnsInfo_whenRetriesWithTimeout() {
        mockWebServer.enqueue(TestDataProvider.partialSuccessResponse().setBodyDelay(DELAY_MILLIS, TimeUnit.MILLISECONDS));

        GetMenuInfoRequest infoRequest = new GetMenuInfoRequest(Set.of("One", "Two", "Three"));
        Mono<GetMenuInfoResponse> menuInfo = menuClient.getMenuInfo(infoRequest);
        StepVerifier.create(menuInfo)
                .expectError(OrderServiceException.class)
                .verify();
    }


    private void assertResponseCorrect(Mono<GetMenuInfoResponse> response) {
        StepVerifier.create(response)
                .expectNextMatches(result -> {
                    List<MenuInfo> menuInfos = result.getMenuInfos();
                    menuInfos.sort(Comparator.comparing(MenuInfo::getName));
                    assertThat(menuInfos)
                            .map(MenuInfo::getName)
                            .containsExactly("One", "Three", "Two");
                    assertThat(menuInfos)
                            .map(MenuInfo::getPrice)
                            .containsExactly(
                                    BigDecimal.valueOf(10.1),
                                    BigDecimal.valueOf(30.3),
                                    null
                            );
                    assertThat(menuInfos)
                            .map(MenuInfo::getIsAvailable)
                            .containsExactly(
                                    true, true, false
                            );
                    return true;
                })
                .verifyComplete();
    }

    private void verifyNumberOfPostRequests(int times) throws Exception {
        for (int i = 0; i < times; i++) {
            // ответы уже готовы и достаются из mockWebServer без задержек
            // делаем timeout, чтобы при некорректной реализации тесты не заблокировались
            RecordedRequest recordedRequest = mockWebServer.takeRequest(1000, TimeUnit.MILLISECONDS);
            assertThat(recordedRequest)
                    .as("Recorded requests: %d, expected: %d", i, times)
                    .isNotNull();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo(props.getMenuInfoPath());
        }
        assertThat(mockWebServer.takeRequest(1000, TimeUnit.MILLISECONDS))
                .as("Expected %d requests, but received more", times).isNull();
    }
}