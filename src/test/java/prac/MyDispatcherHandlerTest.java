package prac;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@SpringBootTest
class MyDispatcherHandlerTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void functional() {
        ServerRequest request = ServerRequest.create(MockServerWebExchange
                                                        .from(MockServerHttpRequest.get("/test").build())
                                                        , HandlerStrategies.withDefaults().messageReaders());

        Mono<Void> result = new MyDispatcherHandler(applicationContext)
                .handle(request);

        StepVerifier.create(result)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void annotated() {
        ServerRequest request = ServerRequest.create(MockServerWebExchange
                                                        .from(MockServerHttpRequest.get("/test2").build())
                                                        , HandlerStrategies.withDefaults().messageReaders());

        Mono<Void> result = new MyDispatcherHandler(applicationContext)
                .handle(request);

        StepVerifier.create(result)
                .expectSubscription()
                .verifyComplete();
    }

}