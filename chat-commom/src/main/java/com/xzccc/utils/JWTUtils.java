//package com.xzccc.server.utils;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.JWTCreator;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Calendar;
//import java.util.Map;
//
//@Component
//public class JWTUtils {
//    /**
//     * 生成token  header.payload.singature
//     */
//    @Value("${jwt.secret}")
//    private String secret;
//
//    @Value("${jwt.exp}")
//    private int exp;
//
//
//    public String getToken(Map<String, Long> map) {
//
//        Calendar instance = Calendar.getInstance();
//        // 默认7天过期
//        instance.add(Calendar.DATE, exp);
//
//        //创建jwt builder
//        JWTCreator.Builder builder = JWT.create();
//
//        // payload
//        map.forEach((k, v) -> {
//            builder.withClaim(k, v);
//        });
//
//        String token = builder.withExpiresAt(instance.getTime())  //指定令牌过期时间
//                .sign(Algorithm.HMAC256(secret));  // sign
//        return token;
//    }
//
//    /**
//     * 验证token  合法性
//     */
//    public DecodedJWT verify(String token) {
//        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
//    }
//
//    public DecodedJWT decodeToken(String token) {
//        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
//    }
//
//}
