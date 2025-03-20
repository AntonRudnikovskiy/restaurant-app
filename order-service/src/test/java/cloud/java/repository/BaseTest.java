package cloud.java.repository;

import cloud.java.config.R2dbcConfig;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Import({R2dbcConfig.class})
@ImportAutoConfiguration({JacksonAutoConfiguration.class})
@Testcontainers
public class BaseTest {
    @Autowired
    private ConnectionFactory connectionFactory;
    @Container
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.1"));

    @DynamicPropertySource
    static void applyProperties(DynamicPropertyRegistry registry) {
        var url = "r2dbc:postgresql://" +
                container.getHost() + ":" +
                container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT) + "/" +
                container.getDatabaseName();

        registry.add("spring.r2dbc.url", () -> url);
        registry.add("spring.r2dbc.username", container::getUsername);
        registry.add("spring.r2dbc.password", container::getPassword);
        registry.add("spring.flyway.url", container::getJdbcUrl);
        registry.add("spring.flyway.user", container::getUsername);
        registry.add("spring.flyway.password", container::getPassword);
    }

    @BeforeEach
    void populateDb(
            @Value("classpath:db.migration/V1__orders_initial_schema.sql") Resource script,
            @Value("classpath:db.migration/insert-orders.sql") Resource resource
    ) {
        executeScriptBlocking(script, resource);
    }

    @AfterEach
    void clearDb(@Value("classpath:db.migration/delete-orders.sql") Resource script) {
        executeScriptBlocking(script);
    }

    private void executeScriptBlocking(final Resource... resources) {
        var populator = new ResourceDatabasePopulator();
        for (Resource resource : resources) {
            populator.addScript(resource);
        }
        populator.populate(connectionFactory).block();
    }
}
