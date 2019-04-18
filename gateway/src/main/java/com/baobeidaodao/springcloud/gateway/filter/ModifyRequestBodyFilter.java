package com.baobeidaodao.springcloud.gateway.filter;

import com.baobeidaodao.springcloud.gateway.constant.FilterConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author DaoDao
 */
@Slf4j
@Component
@Order(value = -90)
public class ModifyRequestBodyFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 尝试从 exchange 的自定义属性中取出缓存到的 body
        Object cachedRequestBodyObject = exchange.getAttributeOrDefault(FilterConstant.CACHED_REQUEST_BODY_OBJECT_KEY, null);
        if (cachedRequestBodyObject != null) {
            byte[] body = (byte[]) cachedRequestBodyObject;
            String string = new String(body);
            log.info("request body:");
            log.info(string);
            // exchange.getAttributes().remove(FilterConstant.CACHED_REQUEST_BODY_OBJECT_KEY);
            DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
            ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    if (body.length > 0) {
                        return Flux.just(dataBufferFactory.wrap(body));
                    }
                    return Flux.empty();
                }
            };
            return chain.filter(exchange.mutate().request(decorator).build());
        }
        // 为空，说明已经读过，或者 request body 原本即为空，不做操作，传递到下一个过滤器链
        return chain.filter(exchange);
    }

}
