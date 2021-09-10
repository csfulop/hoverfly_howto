package hu.fcs.hoverfly;

import io.specto.hoverfly.junit5.HoverflyExtension;
import io.specto.hoverfly.junit5.api.HoverflyCapture;
import io.specto.hoverfly.junit5.api.HoverflyConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
@ExtendWith(HoverflyExtension.class)
@HoverflyCapture(
    config = @HoverflyConfig(
        plainHttpTunneling = true
    ),
    // path needs to be overridden because by default it saves the captures into the test folder
    path = "src/componentTest/resources/hoverfly"
)
class HoverflyCaptureTests {

    @Container
    private static final GenericContainer<?> container =
        new GenericContainer<>("dotronglong/faker:stable")
            .withExposedPorts(3030)
            .withFileSystemBind("src/componentTest/resources/mocks", "/app/mocks");

    @DynamicPropertySource
    private static void registerApiPort(DynamicPropertyRegistry registry) {
        registry.add("api.port", () -> container.getMappedPort(3030));
    }

    @LocalServerPort
    private int port;

    @Test
    void get() {
        // given
        WebTestClient webTestClient = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        // when
        WebTestClient.ResponseSpec response = webTestClient.get().uri("/").exchange();

        // then
        response.expectStatus().isOk();
        List<User> users = response
            .returnResult(User.class)
            .getResponseBody()
            .toStream()
            .collect(Collectors.toList());
        assertThat(users).hasSize(2);
    }
}
