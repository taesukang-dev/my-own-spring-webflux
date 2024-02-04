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

    // MyHandlerMapping 객체들을 저장
    private List<MyHandlerMapping> handlerMappings;

    // DispatcherHandler 의 initStrategies에 대응
    public MyDispatcherHandler(ApplicationContext applicationContext) {
        // applicationContext 에서 MyHandlerMapping 타입의 빈들을 찾아서 handlerMappings 에 저장
        Map<String, MyHandlerMapping> handlerMappingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MyHandlerMapping.class, true, false);
        this.handlerMappings = new ArrayList<MyHandlerMapping>(handlerMappingBeans.values());
    }

    // request 가 들어오면 handlerMappings 에서 handler 를 찾아서 실행
    public Mono<Void> handle(ServerRequest request) {
        if (this.handlerMappings == null) {
            throw new IllegalStateException("MyDispatcherHandler is not initialized");
        } else {
            return Flux.fromIterable(this.handlerMappings) // handlerMappings 을 data stream 으로 변환
                    .concatMap(mapping -> mapping.getHandler(request)) // handlerMappings 에서 handler 를 찾아서 실행
                    .next() // 첫번째 요소만 가져옴
                    .switchIfEmpty(Mono.error(new IllegalStateException("No matching handler")))
                    .flatMap(handler -> handleRequestWith(request, handler)); // handler 를 실행
        }
    }

    // HandlerFunctionAdapter, RequestMappingHandlerAdapter 에 대응
    // Spring WebFlux 는 handler 를 실행하는 주체로 adapter 를 사용한다, adapter 에게 핸들러를 parameter 로 넘기는 방식
    // adapter 는 handler 를 실행하고 결과를 리턴한다, 예제에서는 handleRequestWith 에서 바로 실행
    private Mono<Void> handleRequestWith(ServerRequest request, Object handler) {
        return ((HandlerFunction<?>) handler).handle(request)
                .doOnNext(response -> {
                    Object body = ((EntityResponse) response).entity();
                    log.info("Response body: {}", body);
                })
                .then();
    }
}


