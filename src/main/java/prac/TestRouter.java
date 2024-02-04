package prac;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TestRouter {
    @Bean
    public RouterFunction<?> route() {
        return RouterFunctions.route()
                .GET("/test", request -> ServerResponse.ok().bodyValue("Hello, Router!"))
                .build();
    }
}
