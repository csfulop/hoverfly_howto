package hu.fcs.hoverfly;

import io.specto.hoverfly.junit5.HoverflyExtension;
import io.specto.hoverfly.junit5.api.HoverflyConfig;
import io.specto.hoverfly.junit5.api.HoverflySimulate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(HoverflyExtension.class)
@TestPropertySource(properties = {"api.port = 3333"})
@HoverflySimulate(
    enableAutoCapture = false,
    config = @HoverflyConfig(
        plainHttpTunneling = true
    ),
    source = @HoverflySimulate.Source(
        // by default it tries to put/get capture files from test folder
        // https://docs.hoverfly.io/projects/hoverfly-java/en/latest/pages/junit5/extension.html#simulate
        type = HoverflySimulate.SourceType.FILE,
        value = "src/componentTest/resources/hoverfly/hu_fcs_hoverfly_HoverflySimulateTests.json"
        // NOTE: capture file captured by HoverflyCaptureTests
        // needs to be renamed to match the name of THIS test class
        // and the port needs to be updated because Testcontainers by design uses random port mapping
        )
)
class HoverflySimulateTests {

    @LocalServerPort
    private int port;

    @Test
    void testGetWithHoverflySimulate() {
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
        assertThat(users).hasSize(3);
    }
}
