# my-own-spring-webflux

- DispatcherHandler
- HandlerMapping
  - RouterFunctionMapping
  - AbstractHandlerMapping

```java
// RouterFunctionMapping
protected void initRouterFunctions() {
    List<RouterFunction<?>> routerFunctions = this.routerFunctions();
    this.routerFunction = (RouterFunction)routerFunctions.stream().reduce(RouterFunction::andOther).orElse((Object)null);
    this.logRouterFunctions(routerFunctions);
}

private List<RouterFunction<?>> routerFunctions() {
    return (List)this.obtainApplicationContext().getBeanProvider(RouterFunction.class).orderedStream().map((router) -> {
        return router;
    }).collect(Collectors.toList());

    
// AbstractHandlerMapping
public Mono<Object> getHandler(ServerWebExchange exchange) {
    return this.getHandlerInternal(exchange).map((handler) -> {
        if (this.logger.isDebugEnabled()) {
            Log var10000 = this.logger;
            String var10001 = exchange.getLogPrefix();
            var10000.debug(var10001 + "Mapped to " + handler);
        }

        ServerHttpRequest request = exchange.getRequest();
        if (this.hasCorsConfigurationSource(handler) || CorsUtils.isPreFlightRequest(request)) {
            CorsConfiguration config = this.corsConfigurationSource != null ? this.corsConfigurationSource.getCorsConfiguration(exchange) : null;
            CorsConfiguration handlerConfig = this.getCorsConfiguration(handler, exchange);
            config = config != null ? config.combine(handlerConfig) : handlerConfig;
            if (config != null) {
                config.validateAllowCredentials();
                config.validateAllowPrivateNetwork();
            }

            if (!this.corsProcessor.process(config, exchange) || CorsUtils.isPreFlightRequest(request)) {
                return NO_OP_HANDLER;
            }
        }

        return handler;
    });
}

// BeanFactoryUtils
// RouterFunction, Handler -> HandlerMapping
public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
    Assert.notNull(lbf, "ListableBeanFactory must not be null");
    Map<String, T> result = new LinkedHashMap(4);
    result.putAll(lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit));
    if (lbf instanceof HierarchicalBeanFactory hbf) {
        BeanFactory var7 = hbf.getParentBeanFactory();
        if (var7 instanceof ListableBeanFactory pbf) {
            Map<String, T> parentResult = beansOfTypeIncludingAncestors(pbf, type, includeNonSingletons, allowEagerInit);
            parentResult.forEach((beanName, beanInstance) -> {
                if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
                    result.put(beanName, beanInstance);
                }

            });
        }
    }

    return result;
}
```