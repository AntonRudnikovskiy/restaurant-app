package cloud.java.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "external")
public class OrderServiceProps {
    private final String menuServiceUrl;
    private final String menuInfoPath;
    private final Duration defaultTimeout;
    private final Duration retryBackoff;
    private final int retryCount;
    private final double retryJitter;
}
