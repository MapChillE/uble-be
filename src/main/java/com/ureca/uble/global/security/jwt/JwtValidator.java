package com.ureca.uble.global.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtValidator {

	@Value("${jwt.secret}")
	private String secret;

	public boolean validateToken(String token) {
		try{
			Jwts.parser()
				.setSigningKey(secret.getBytes())
				.parseClaimsJws(token);
			return true;
		} catch (Exception e){
			System.out.println(token);
			System.out.println(secret);
			System.out.println("🔴 JWT 검증 실패:");
			e.printStackTrace(); // 반드시 추가
			return false;
		}
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = Jwts.parser()
			.setSigningKey(secret.getBytes())
			.parseClaimsJws(token)
			.getBody();
		return Long.valueOf(claims.getSubject());
	}

	public String extractAccessToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");

		if(header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}

		return null;
	}

}
