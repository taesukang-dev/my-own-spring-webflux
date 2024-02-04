package prac;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class MyDispatcherHandler {

    private List<MyHandlerMapping> handlerMappings;

    // initStrategies
    public MyDispatcherHandler(ApplicationContext applicationContext) {
        Map<String, MyHandlerMapping> handlerMappingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MyHandlerMapping.class, true, false);
        this.handlerMappings = new ArrayList<MyHandlerMapping>(handlerMappingBeans.values());
    }

    public Mono<Void> handle(ServerRequest request) {
        if (this.handlerMappings == null) {
            throw new IllegalStateException("MyDispatcherHandler is not initialized");
        } else {
            return Flux.fromIterable(this.handlerMappings)
                    .concatMap(mapping -> mapping.getHandler(request))
                    .next()
                    .switchIfEmpty(Mono.error(new IllegalStateException("No matching handler")))
                    .flatMap(handler -> handleRequestWith(request, handler));
        }
    }

    // HandlerFunctionAdapter, RequestMappingHandlerAdapter
    private Mono<Void> handleRequestWith(ServerRequest request, Object handler) {
        return ((HandlerFunction<?>) handler).handle(request)
                .doOnNext(response -> {
                    Object body = ((EntityResponse) response).entity();
                    log.info("Response body: {}", body);
                })
                .then();
    }
}


