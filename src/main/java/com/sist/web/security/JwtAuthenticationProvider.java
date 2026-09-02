package com.sist.web.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtAuthenticationProvider {
	/*
	//임의의 key 생성
	public String createSecretKey() 
	{
		SecretKey key = Keys.secretKeyFor(
			SignatureAlgorithm.HS256
		);
		
		String secretKey = Encoders.BASE64.encode(key.getEncoded());
		
		return secretKey;
	}*/
	
	private final String SECRET="one-secret-key-two-secret-key-three-secret-key";
	
	//토큰 생성
	public String createToken(String username,String role)
	{
		// payload => {sub:"admin",role:"ROLE_ADMIN"}
		return Jwts.builder()
				.setSubject(username) // 사용자 아이디
				.claim("role", role) // 사용자 권한
				.setIssuedAt(new Date()) //jwt 발급 시간
				.setExpiration(new Date(System.currentTimeMillis()+3600000)) //만료 시간
				.signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
				.compact();
				
	}
	
	//사용자 ID를 추출
	public String getUsername(String token)
	{
		return Jwts.parserBuilder()
				.setSigningKey(SECRET.getBytes())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	
	//위조 확인
	public boolean validate(String token)
	{
		try
		{
			Jwts.parserBuilder()
				.setSigningKey(SECRET.getBytes())
				.build()
				.parse(token);
			
			return true;
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
