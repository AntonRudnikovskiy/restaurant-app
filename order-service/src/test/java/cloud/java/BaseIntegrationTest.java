package cloud.java;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static cloud.java.TestConstants.DELAY_MILLIS;
import static cloud.java.TestConstants.MENU_INFO_PATH;
import static cloud.java.TestDataProvider.readPartiallySuccessfulResponse;
import static cloud.java.TestDataProvider.readSuccessfulResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
public class BaseIntegrationTest extends BaseTest {
    @RegisterExtension
    protected static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void applyProperties(DynamicPropertyRegistry registry) {
        registry.add("external.menu-service-url", wiremock::baseUrl);
    }

    protected void prepareStubForServiceUnavailable() {
        wiremock.stubFor(post(MENU_INFO_PATH)
                .willReturn(serviceUnavailable()));
    }

    protected void prepareStubForSuccessWithTimeout() {
        var responseBody = readSuccessfulResponse();
        wiremock.stubFor(post(MENU_INFO_PATH)
                .willReturn(okJson(responseBody).withFixedDelay(DELAY_MILLIS))
        );
    }

    protected void prepareStubForPartialSuccess() {
        var responseBody = readPartiallySuccessfulResponse();
        wiremock.stubFor(post(MENU_INFO_PATH)
                .willReturn(okJson(responseBody))
        );
    }

    protected void prepareStubForSuccess() {
        var responseBody = readSuccessfulResponse();
        wiremock.stubFor(post(MENU_INFO_PATH)
                .willReturn(okJson(responseBody)));
    }
}
