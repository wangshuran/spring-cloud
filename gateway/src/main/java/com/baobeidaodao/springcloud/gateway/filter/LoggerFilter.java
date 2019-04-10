package com.baobeidaodao.springcloud.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaoDao
 */
@Slf4j
@Component
@Order(value = 0)
public class LoggerFilter implements GlobalFilter {

    @Resource
    private ObjectMapper objectMapper;

    private Map<String, Object> logger;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String id = request.getId();
        RequestPath path = request.getPath();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        /// MultiValueMap<String, HttpCookie> cookies = serverHttpRequest.getCookies();
        /// InetSocketAddress remoteAddress = serverHttpRequest.getRemoteAddress();
        /// SslInfo sslInfo = serverHttpRequest.getSslInfo();
        /// Flux<DataBuffer> body = serverHttpRequest.getBody();
        /// HttpMethod httpMethod = serverHttpRequest.getMethod();
        HttpHeaders requestHeaders = request.getHeaders();
        String methodValue = request.getMethodValue();
        URI uri = request.getURI();

        logger = new HashMap<>(1 << 4);
        logger.put("filter", "preLogger");
        logger.put("id", id);
        logger.put("path", path.value());
        logger.put("queryParams", queryParams);
        logger.put("requestHeaders", requestHeaders);
        logger.put("method", methodValue);
        logger.put("uri", uri.toString());
        try {
            log.info(objectMapper.writeValueAsString(logger));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            /// MultiValueMap<String, ResponseCookie> cookies = response.getCookies();
            HttpStatus statusCode = response.getStatusCode();
            HttpHeaders responseHeaders = response.getHeaders();
            // todo response body

            logger = new HashMap<>(1 << 4);
            logger.put("filter", "postLogger");
            logger.put("responseHeaders", responseHeaders);
            if (statusCode != null) {
                logger.put("statusCode", statusCode.value());
            }
            try {
                log.info(objectMapper.writeValueAsString(logger));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }));
    }

}
