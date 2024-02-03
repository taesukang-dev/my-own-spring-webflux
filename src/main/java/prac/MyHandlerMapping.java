package prac;

import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public interface MyHandlerMapping {
    Mono<HandlerFunction<?>> getHandler(ServerRequest request);
}
