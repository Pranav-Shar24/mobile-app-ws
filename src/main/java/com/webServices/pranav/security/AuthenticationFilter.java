package com.webServices.pranav.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webServices.pranav.SpringApplicationContext;
import com.webServices.pranav.service.UserService;
import com.webServices.pranav.shared.dto.UserDto;
import com.webServices.pranav.ui.model.request.UserLoginRequestModel;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authManager;

	public AuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		try {
			UserLoginRequestModel credentials = new ObjectMapper()
					.readValue(request.getInputStream(), UserLoginRequestModel.class); 
			return authManager.authenticate(
					new  UsernamePasswordAuthenticationToken
					(credentials.getEmail(), 
					credentials.getPassword(), 
					new ArrayList<>()));
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String userName = ((User) authResult.getPrincipal()).getUsername();
		
		String token = Jwts.builder().setSubject(userName)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getSecretToken())
				.compact();
		
		UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
		UserDto user = userService.getUser(userName);
		
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
		response.addHeader("UserID ", user.getUserId());
	}
}
