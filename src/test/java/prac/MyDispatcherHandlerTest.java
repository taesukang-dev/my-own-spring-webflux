package prac;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyDispatcherHandlerTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void routerfunction() throws InterruptedException {
        MockServerHttpRequest mockRequest = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange mockExchange = MockServerWebExchange.from(mockRequest);
        ServerRequest request = ServerRequest.create(mockExchange, HandlerStrategies.withDefaults().messageReaders());

        new MyDispatcherHandler(applicationContext)
                .handle(request)
                .subscribe();

        Thread.sleep(1000);
    }

}