package com.expert.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Map;

public class JwtUtil {


    /**
     * 统一的密钥生成方法
     */
    private static SecretKey generateSecretKey(String secretKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(secretKey.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate secret key", e);
        }
    }
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {

        try {
            // 使用SHA-256将任意密钥扩展为256位
            SecretKey key = generateSecretKey(secretKey);

            long expMillis = System.currentTimeMillis() + ttlMillis;
            Date exp = new Date(expMillis);

            return Jwts.builder()
                    .claims(claims)
                    .expiration(exp)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JWT", e);
        }
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        //秘钥
        SecretKey key = generateSecretKey(secretKey);

        //解析
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
