package com.poetryapp.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poetryapp.common.constant.ResponseCode;
import com.poetryapp.common.response.ApiResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 认证全局过滤器
 * 白名单路径直接放行；其余路径验证 Bearer Token
 */
@Log4j2
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    /** 不需要认证的路径前缀 */
    private static final List<String> WHITE_LIST = List.of(
            "/api/auth/",
            "/actuator/"
    );

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int getOrder() {
        return -200;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 白名单直接放行
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return writeError(exchange, ResponseCode.UNAUTHORIZED, "请先登录");
        }

        String token = authHeader.substring(7);
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 将用户信息写入下游请求头
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-User-Id",   claims.get("userId", Long.class).toString())
                    .header("X-User-Phone", claims.get("phone", String.class))
                    .header("X-User-Role",  claims.get("role", String.class))
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (ExpiredJwtException e) {
            log.info("Token 已过期, path={}", path);
            return writeError(exchange, ResponseCode.TOKEN_EXPIRED, "登录已过期，请重新登录");
        } catch (JwtException e) {
            log.warn("Token 无效, path={}, msg={}", path, e.getMessage());
            return writeError(exchange, ResponseCode.UNAUTHORIZED, "Token 无效，请重新登录");
        }
    }

    private Mono<Void> writeError(ServerWebExchange exchange, int code, String message) {
        ServerHttpResponse resp = exchange.getResponse();
        resp.setStatusCode(HttpStatus.OK);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Void> body = ApiResponse.fail(code, message);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = "{\"code\":500,\"message\":\"系统错误\"}".getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = resp.bufferFactory().wrap(bytes);
        return resp.writeWith(Mono.just(buffer));
    }
}
