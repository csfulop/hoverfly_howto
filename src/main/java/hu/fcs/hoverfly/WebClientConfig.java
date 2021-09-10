package hu.fcs.hoverfly;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;


@Configuration
public class WebClientConfig {
    private final WebClient.Builder builder;

    public WebClientConfig(WebClient.Builder builder) {
        this.builder = builder;
    }

    @Bean
    public WebClient getWebClient() {
        return getBuilderWithProxy(builder.clone()).build();
    }

    private WebClient.Builder getBuilderWithProxy(WebClient.Builder builder) {
        String proxyHost = null;
        int proxyPort = 0;
        if (System.getProperty("http.proxyHost") != null) {
            proxyHost = System.getProperty("http.proxyHost");
            proxyPort = Integer.parseInt(System.getProperty("http.proxyPort", "80"));
        }
        if (proxyHost != null) {
            builder = builder.clientConnector(getHttpConnector(proxyHost, proxyPort));
        }
        return builder;
    }

    private ReactorClientHttpConnector getHttpConnector(String proxyHost, int proxyPort) {
        return new ReactorClientHttpConnector(
            HttpClient.create()
                .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                    .host(proxyHost)
                    .port(proxyPort)));
    }
}
