package com.webServices.pranav.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.webServices.pranav.service.UserService;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private final UserService USER_DETAILS;
	private final BCryptPasswordEncoder B_CRYPT;

	public WebSecurity(UserService uSER_DETAILS, BCryptPasswordEncoder b_CRYPT) {

		this.USER_DETAILS = uSER_DETAILS;
		this.B_CRYPT = b_CRYPT;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable().authorizeRequests()
		.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
		.permitAll()
		.anyRequest()
		.authenticated()
		.and()
		.addFilter(getAuthenticationFilter())
		.addFilter(new AuthorizationFilter(authenticationManager()))
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(USER_DETAILS)
		.passwordEncoder(B_CRYPT);
	}

	public AuthenticationFilter getAuthenticationFilter () throws Exception {
		
		final AuthenticationFilter filter =  new AuthenticationFilter(authenticationManager());
		filter.setFilterProcessesUrl("/users/login");
		return filter;
		
	}
}
