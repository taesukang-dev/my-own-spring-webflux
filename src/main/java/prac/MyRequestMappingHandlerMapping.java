package prac;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// RequestMappingHandlerMapping -> RequestMappingInfoHandlerMapping -> AbstractHandlerMethodMapping -> AbstractHandlerMapping
// AbstractHandlerMethodMapping, RequestMappingHandlerMapping -> @RequestMapping 을 관리
@Configuration
public class MyRequestMappingHandlerMapping implements MyHandlerMapping{

    // mappingRegistry 에 handler 를 저장
    private Map<Object, Set<Method>> mappingRegistry = new HashMap<>();

    // AbstractHandlerMethodMapping.initHandlerMethods 에 대응
    // applicationContext 에서 @Controller 를 찾아서 mappingRegistry 에 저장
    public MyRequestMappingHandlerMapping(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        int length = beanNames.length;

        for (int i = 0; i < length; i++) {
            String beanName = beanNames[i];
            if (!beanName.startsWith("scopedTarget.")) {
                Class<?> beanType = null;
                try {
                    beanType = applicationContext.getType(beanName);
                } catch (Throwable t) {
                    // ignore
                }

                if (beanType != null && isHandler(beanType)) {
                    // AbstractHandlerMethodMapping, detectHandlerMethods 애 댜웅
                    Class<?> userClass = ClassUtils.getUserClass(beanType);
                    Set<Method> methods = MethodIntrospector.selectMethods(userClass, (ReflectionUtils.MethodFilter) (method) -> {
                        // RequestMappingHandlerMapping, getMappingforMethod 에 대응
                        // 다른 annotation 또한 mapping 이 필요하면 GetMapping -> requestMapping 으로 변경
                        return AnnotatedElementUtils.hasAnnotation(method, GetMapping.class);
                    });

                    // AbstractHandlerMethodMapping, registerHandlerMethod 에 대응
                    Object bean = applicationContext.getBean(beanName);
                    mappingRegistry.put(bean, methods);
                }
            }
        }
    }

    @Override
    public Mono<Object> getHandler(ServerRequest request) {
        // AbstractHandlerMethodMapping, getHandlerInternal 에 대응
        // {lookupHandlerMethod, createHandlerMethod}
        for (Map.Entry<Object, Set<Method>> entry : mappingRegistry.entrySet()) {
            Object bean = entry.getKey();
            Set<Method> methods = entry.getValue();
            for (Method method : methods) {
                if (matches(request, method)) {
                    // HandlerFunction을 생성하고 Mono 로 reutnr, MyDispatcherHandler.handle 에서 lazy evaluation
                    // handle 메소드는 ServerRequest 를 받아 invokeMethod 를 실행
                    return Mono.just((HandlerFunction<ServerResponse>) request1 -> invokeMethod(bean, method, request1));
                }
            }
        }
        return Mono.empty();
    }

    private boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
    }

    private boolean matches(ServerRequest request, Method method) {
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            String path = getMapping.value()[0];
            return request.path().equals(path);
        }
        return false;
    }

    private Mono<ServerResponse> invokeMethod(Object bean, Method method, ServerRequest request) {
        try {
            Object result = method.invoke(bean);
            if (result instanceof Mono) {
                return ((Mono) result).flatMap(res -> {
                    if (res instanceof ServerResponse) {
                        return Mono.just((ServerResponse) res);
                    } else {
                        return ServerResponse.ok().bodyValue(res);
                    }
                });
            } else {
                return ServerResponse.ok().bodyValue(result);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            return Mono.error(e);
        }
    }

}
