package prac;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RestController
public class TestRestController {

    @GetMapping("/test2")
    public Mono<ServerResponse> test() {
        return ServerResponse
                .ok()
                .bodyValue("Hello, RestController!");
    }
}
