package prac;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

// RouterFunctionMapping
@Configuration
public class MyRouterFunctionMapping implements MyHandlerMapping{
    private RouterFunction<?> routerFunction;

    public MyRouterFunctionMapping(ApplicationContext applicationContext) {
        // applicationContext 에서 RouterFunction 타입의 빈들을 찾아서 routerFunction 에 저장
        routerFunction = applicationContext.getBeanProvider(RouterFunction.class)
                            .orderedStream()
                            .toList()
                            .stream().reduce(RouterFunction::andOther)
                            .orElse(null);
    }

    @Override
    public Mono<HandlerFunction<?>> getHandler(ServerRequest request) {
        // routerFunction 에서 request 에 맞는 handler 를 찾아서 실행할 수 있는 HandlerFunction 으로 return
        // routerFunction 은 내부적으로 request 에 맞는 handler 를 찾아서 HandlerFunction 으로 return
        // DefaultRouterFunction.route 에서 찾아서 return
        Mono<? extends HandlerFunction<?>> route = routerFunction.route(request);
        return route.flatMap(Mono::just);
    }
}
