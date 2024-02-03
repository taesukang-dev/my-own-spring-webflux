package prac;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Configuration
public class MyRouterFunctionMapping implements MyHandlerMapping{
    private RouterFunction<?> routerFunction;

    public MyRouterFunctionMapping(ApplicationContext applicationContext) {
        routerFunction = applicationContext.getBeanProvider(RouterFunction.class)
                            .orderedStream()
                            .toList()
                            .stream().reduce(RouterFunction::andOther)
                            .orElse(null);
    }

    @Override
    public Mono<HandlerFunction<?>> getHandler(ServerRequest request) {
        Mono<? extends HandlerFunction<?>> route = routerFunction.route(request);
        return route.flatMap(Mono::just);
    }
}
