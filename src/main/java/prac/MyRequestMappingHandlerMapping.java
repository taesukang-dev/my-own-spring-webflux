package prac;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

//@Configuration
public class MyRequestMappingHandlerMapping implements MyHandlerMapping{
    @Override
    public Mono<HandlerFunction<?>> getHandler(ServerRequest request) {
        return null;
    }
}
