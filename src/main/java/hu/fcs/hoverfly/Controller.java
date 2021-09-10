package hu.fcs.hoverfly;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;


@RestController
public class Controller {

    @Value("${api.port:3030}")
    private int apiPort;

    private final WebClient webClient;

    public Controller(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/")
    List<User> get() {
        return webClient
            .get()
            .uri(format("http://localhost:%d/v1/users", apiPort))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(User.class)
            .toStream()
            .collect(Collectors.toList());
    }
}
