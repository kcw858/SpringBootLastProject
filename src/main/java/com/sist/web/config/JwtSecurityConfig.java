package com.sist.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sist.web.security.JwtAuthenticationFilter;
import com.sist.web.security.JwtAuthenticationProvider;
import com.sist.web.service.CustomUserDetailsService;
/*
 *  	사용자
 *  	  | /member/login
 *  	login.html
 *  	--------
 *  	  | id,pwd
 */
@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {
	
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(
			CustomUserDetailsService uds,JwtAuthenticationProvider provider)
	{
		return new JwtAuthenticationFilter(uds,provider);
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationFilter filter) throws Exception
	{
		http
			.csrf(csrf -> csrf.disable()) //위조 방지
			.sessionManagement(session -> 
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.formLogin(form -> form.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/","/login","/member").permitAll()
				.requestMatchers("/admin").hasRole("ADMIN")
				.anyRequest().permitAll()
			)
			.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	//비밀번호 암호화
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
		// => 암호화 encode()
		// => 검색 matcher()
		// => 같은 비밀번호 => 여러개의 패턴을 이용한다
	}
	
	//인가등록
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
	{
		return config.getAuthenticationManager();
	}
	
	
}
