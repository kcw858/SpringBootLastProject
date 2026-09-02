package com.sist.web.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sist.web.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/*
 *  사용자 정보 저장 : UserDetailsService
 *  토큰 생성 / 유효성 검사 : Provider
 *  통합 : Filter
 *  권한 / URL 접근 : Config
 *  실제 사용자 요청 => Controller
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private final CustomUserDetailsService uds;
	private final JwtAuthenticationProvider provider;
	
	public JwtAuthenticationFilter(CustomUserDetailsService uds,JwtAuthenticationProvider provider)
	{
		this.uds = uds;
		this.provider = provider;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = null;
		//1. header 2. cookie
		// -> Vue,React요청시 header로 넘어온다
		
		String header = request.getHeader("Authorization"); // JSON {"Authorization":"Bearer dwefsfeqw13243wef..."}
														    //				subject:id    role:권한
		
		if(header != null && header.startsWith("Bearer "))
		{
			token= header.substring(7);
		}
		
		//헤더에 없으면 cookie 처리
		if(token == null && request.getCookies() != null)
		{
			for(Cookie cookie : request.getCookies())
			{
				if("accessToken".equals(cookie.getName()))
				{
					token = cookie.getValue();
					break;
				}
			}
		}
		
		//JWT 검증
		if(token!=null && provider.validate(token))
		{
			// 사용자 정보 조회
			String username = provider.getUsername(token);
			
			UserDetails user = uds.loadUserByUsername(username);
			
			//Security 인증
			UsernamePasswordAuthenticationToken auth = 
					new UsernamePasswordAuthenticationToken(
							user,
							null,
							user.getAuthorities()
							
					);
			
			//저장 => Security에서 관리
			SecurityContextHolder
				.getContext()
				.setAuthentication(auth);
		}
		
		// => 다음 Filter 사용 : Controller 실행
		filterChain.doFilter(request, response);
		
		//요청 => DispatcherServlet
		//요청 => Security = DispatcherServlet
	}
	
}
