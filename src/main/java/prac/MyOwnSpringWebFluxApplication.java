package prac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class MyOwnSpringWebFluxApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyOwnSpringWebFluxApplication.class, args);
    }
}

@RestController
class TestRestController {

    @GetMapping("/test2")
    public Mono<ServerResponse> test() {
        return ServerResponse
                .ok()
                .bodyValue("Hello, RestController!");
    }
}

@Configuration
class TestRouter {
    @Bean
    public RouterFunction<?> route() {
        return RouterFunctions.route()
                .GET("/test", request -> ServerResponse.ok().bodyValue("Hello, Router!"))
                .build();
    }
}