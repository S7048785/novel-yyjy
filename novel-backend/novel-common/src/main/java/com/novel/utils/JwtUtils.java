package com.novel.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;


/**
 * @author YYJY
 */
@Component
public class JwtUtils {
	
	private static String SECRET_KEY;
	
	@Value("${novel.jwt.secret-key}")
	public void setSecretKey(String secretKey) {
		SECRET_KEY = secretKey;
	}
	
	private static long ttlMillis = 7200000;
	
	
	// 生成 Token
	public static String createJwt(String userId) {
		Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
		return JWT.create()
				       .withSubject(userId)
				       // 签发时间
				       .withIssuedAt(new Date())
				       // 过期时间
				       .withExpiresAt(new Date(System.currentTimeMillis() + ttlMillis))
				       // 签名
				       .sign(algorithm);
	}
	
	// 验证并解析 Token
	public static String parseJwt(String token) {
		Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
		
		return JWT.require(algorithm)
				       .build()
				       // 自动验证签名和过期时间
				       .verify(token)
				       .getSubject();
	}
}