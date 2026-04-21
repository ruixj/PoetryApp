package com.poetryapp.common.utils;

import com.poetryapp.common.constant.ResponseCode;
import com.poetryapp.common.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 密钥通过构造器注入，供各服务通过 @Bean 实例化
 */
@Log4j2
public class JwtUtils {

    private static final long ACCESS_TOKEN_TTL_MS = 24L * 60 * 60 * 1000;   // 24 h

    private final SecretKey secretKey;

    public JwtUtils(String base64Secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    /**
     * 生成 AccessToken
     */
    public String generateToken(Long userId, String phone, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TTL_MS))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析 Claims，过期则抛出 TOKEN_EXPIRED 业务异常
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.info("Token 已过期");
            throw new BusinessException(ResponseCode.TOKEN_EXPIRED, "登录已过期，请重新登录");
        } catch (JwtException e) {
            log.warn("Token 无效: {}", e.getMessage());
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "Token 无效，请重新登录");
        }
    }

    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    public String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    public boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
}
