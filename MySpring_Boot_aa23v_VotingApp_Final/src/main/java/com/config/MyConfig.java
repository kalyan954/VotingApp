package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class MyConfig {

	@Autowired
	private AuthenticationSuccessHandler customSuccessHandler;

	@Bean
	public UserDetailsServiceImpl userDetailsServiceImpl()
	{
		return new UserDetailsServiceImpl();
	}

	@Bean
	@SuppressWarnings("deprecation")
	public PasswordEncoder passwordEncoder()
	{
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsServiceImpl());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		http
			.authenticationProvider(authenticationProvider())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/user/**", "/candidate/**").hasRole("NORMAL")
				.requestMatchers("/**").permitAll()
			)
			.formLogin(form -> form
				.loginPage("/signin")
				.loginProcessingUrl("/dologin")
				.successHandler(customSuccessHandler)
			)
			.logout(logout -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/signin?logout")
			);

		return http.build();
	}

}
