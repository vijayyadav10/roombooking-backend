package com.vizz.roombooking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

	// The method configureGlobal() accepts an argument of
	// AuthenticationManagerBuilder which consists a
	// method inMemoryAuthentication() that creates a user with password and roles.
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("matt").password("{noop}secret").authorities("ROLE_ADMIN").and()
				.withUser("jane").password("{noop}secret").authorities("ROLE_USER");
	}

	// The configure(HttpSecurity) method defines which URL paths should be secured
	// and which should not.
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/*
		 * These work from top to bottom so first of all to access /api/basicAuth/**,
		 * that's secured with basic authentication and you've got have any kind of role
		 * unless you're doing the options, then that's open. For all the other APIs we
		 * go down here. Anything with options is fine, a get on bookings is fine, but
		 * to get anything else you need to be logged in and of course were using JWT
		 * authentication and to do anything other than a get you need to be an
		 * administrator.
		 */

		// Basic Authentication
		http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/api/basicAuth/**").permitAll()
				.antMatchers("/api/basicAuth/**").hasAnyRole("ADMIN", "USER").and().httpBasic();

		// H2 Configuration for solving issue of: localhost refused to connect
		http.headers().frameOptions().disable();
		
		// Bearer Authentication
		http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/bookings/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "USER")
				.antMatchers("/api/**").hasRole("ADMIN")
				.and()
				.addFilter(new JWTAuthorizationFilter(authenticationManager()));
	}
}

/*
 * httpBasic. That tells Spring to use basic authentication. The httpBasic
 * method tells Spring that it needs to look for an authorization header. That
 * header should say Basic, as the authorization type, and then it can decode
 * the base64 encoded string contained in that header, and see if it matches one
 * of our known users. If the user is valid, it creates an authentication object
 * representing that user, which we saw a few moments ago. We, unfortunately,
 * don't have a handy method like this, for JWT authentication, so we're going
 * to have to create our own.
 */

/*
 * Configuring Spring to use bearer authentication***
 * 
 * So, what we'll do is create some rather complex looking code that is going to
 * replicate this httpBasic procedure for JWT authentication. We'll first need
 * to look for the authorization header, we'll look to see if it's a bearer
 * authorization, and if it is, we'll take the token contained in that header
 * and use our service to validate it. If it turns out to be a valid token,
 * then, we're going to create the Spring authentication object and use it to
 * store the information about the user that we obtained from the token. We're
 * about to code this up in com.vizz.roombooking.config.JWTAuthorizationFilter
 * file.
 */
