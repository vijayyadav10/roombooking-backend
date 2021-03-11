package com.vizz.roombooking.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vizz.roombooking.services.JWTService;

@RestController
@RequestMapping("api/basicAuth")
public class ValidateUserController {

	@Autowired
	JWTService jwtService;

	@RequestMapping("validate")
	public Map<String, String> userIsValid(HttpServletResponse response) {

		/*
		 * As part of the process of checking the username and password Spring will have
		 * stored the username and the user's role, the two variables that we need, to
		 * include in the payload of our token in an object called #Authentication. So
		 * we want to look at that object to find out this data. SecurityContextHolder
		 * Which is a spring object;
		 */
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		/*
		 * what we should be able to do is call getPrincipal on it. And that's going to
		 * return an object which should be a user. Now that's not one of our
		 * users(developer's user) that we've defined, it's a specific user class from
		 * Spring.
		 */
		
		User currentUser = (User) auth.getPrincipal();
		System.out.println("XXXXXXX" + currentUser.getUsername() + currentUser.getAuthorities().toArray()[0].toString().substring(5));
		String name = currentUser.getUsername();
		String role = currentUser.getAuthorities().toArray()[0].toString().substring(5);

		String token = jwtService.generateToken(name, role);

		Map<String, String> results = new HashMap<>();
		results.put("result", "ok");

		Cookie cookie = new Cookie("token", token);
		cookie.setPath("/api");
		/*
		 * cookie.setHttpOnly(true). If we do that, it means that we can't reference
		 * that cookie in JavaScript. The browser will store the cookie and send it back
		 * to the server, but it won't let us view the content of the cookie.
		 */
		cookie.setHttpOnly(true);
		//That means that this cookie should only be sent over SSL. 
		//cookie.setSecure(true);
		//cookies a limited lifespan, the value here is in seconds.
		cookie.setMaxAge(1800);
		response.addCookie(cookie);
		return results;
		// return "{\"result\" : \"ok\"}";
	}

}