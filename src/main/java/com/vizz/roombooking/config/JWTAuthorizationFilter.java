package com.vizz.roombooking.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vizz.roombooking.services.JWTService;

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
 * about to code this up,
 */

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager am) {
		super(am);
	}
	
	JWTService jwtService;

	/*
	 * Now, because this is a filter, it's actually going to run on every single
	 * request that we make to the server, not just the ones that we want to ensure
	 * are secured with JWT, so when we code this up, we'll need to ensure that we
	 * just exit with no issues from this filter if there isn't the authentication
	 * header that we are looking for.
	 */

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		Cookie[] cookies = request.getCookies();
		if(cookies == null || cookies.length == 0) {
			chain.doFilter(request, response);
			return;
		}
		
		Cookie tokenCookie = null;
		for(Cookie cookie: cookies) {
			if(cookie.getName().equals("token")) {
				tokenCookie = cookie;
			}
		}
		
		if (tokenCookie == null) {
			chain.doFilter(request, response);
			return;
		}
		
		/*
		 * the first thing we're going to do in here is check to see if there is a
		 * matching header.
		 */
//		String header = request.getHeader("Authorization");
//		if (header == null || !header.startsWith("Bearer")) {
//			chain.doFilter(request, response);
//			return;
//		}


		
		/*
		 * we'll want to do, then, in this filter is to get access to the JWT service so
		 * that we can call its validate method, but because this is a filter we can't
		 * use dependency injection to get a reference to our service. Instead, we'll
		 * need to get a reference from Spring.
		 */
		if (jwtService == null) {
			/*
			 * we'll get an object of type ServletContext, which we'll call servletContext,
			 * and we can get that from the request. Next, we want to get an object of type
			 * WebApplicationContext. Now that we've got this WebApplicationContext, we can
			 * call getBean, and the bean that we want is our JWTService, we'll just put in
			 * .class there, and that should give us our instance of JWTService. You might
			 * be wondering, at this point, why didn't I just create a new JWTService? The
			 * reason for that, of course, is because we must have only one instance of our
			 * JWTService class, because every time it's generated we're going to get a new
			 * set of private and public keys, and we absolutely don't want that.
			 * 
			 */
			ServletContext servletContext = request.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			jwtService = wac.getBean(JWTService.class);
		}

		/*
		 * The next step is to get an object of type
		 * UsernamePasswordAuthenticationToken. And that is a spring object. And that's
		 * what we're going to use to store the information about our user. We'll be
		 * able to store user's name and their role in this token. We won't be storing a
		 * password, but that really doesn't matter.
		 */
		UsernamePasswordAuthenticationToken authentication = getAuthentication(tokenCookie.getValue());

		/*
		 * security context holder and that's how we tell spring, having gone through
		 * this process, this filter has set the user's details from analysing token.So
		 * in other words, this is going to somehow call our JWT service.
		 */
		SecurityContextHolder.getContext().setAuthentication(authentication);
		/*
		 * finally in here, we'll be able to say carry on, doing the processing. So that
		 * is the chain doFilter.
		 */
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(String jwtToken) {
		/*
		 * So we're going to take the header which contains that JSON web token, we're
		 * going to validate the token, and from it, we'll extract the JSON data that
		 * contains the user's name and role and insert those into one of these
		 * UsernamePasswordAuthenticationToken and return that from this method.
		 */
		// let's extract the token from the header first of all
//		String jwtToken = header.substring(7);

		/*
		 * so now, we're going to start by trying to validate our token using the method
		 * that we wrote on our JWT service. Now, that is either going to give us a
		 * return value, which will be the payload. Or it's going to throw an error. And
		 * if it throws an error, that means that the token wasn't valid, so we need to
		 * think about what are we going to do in that instance. Well actually, what
		 * we'll want to do is return a null. We'll say that there is no valid
		 * authentication. The value of this authentication object is null. And that
		 * just means we don't have an authenticated user.
		 */
		try {

			String payload = jwtService.validateToken(jwtToken);
			/*
			 * so we've got a valid token at this point, now, what we want to do is to
			 * analyse it to find out what is the user's name and role. So, we'll get a JSON
			 * Parser to do that because this payload is a JSON string.
			 */
			JsonParser parser = JsonParserFactory.getJsonParser();
			Map<String, Object> payloadMap = parser.parseMap(payload);
			String user = payloadMap.get("user").toString();
			String role = payloadMap.get("role").toString();

			List<GrantedAuthority> roles = new ArrayList<>();
			
			GrantedAuthority ga = new GrantedAuthority() {

				@Override
				public String getAuthority() {

					/*
					 * the role that we've got here(payloadMap.get("role").toString();). So that
					 * will be either user or admin.
					 */
					return "ROLE_" + role;
				}
			};

			roles.add(ga);

			/*
			 * now, in order to set the user and the role in our
			 * UsernamePasswordAuthenticationToken, what actually, what we need is an array
			 * of object type GrantedAuthority. So, just to make that clear, what we're
			 * going to be returning is a new username password authentication token that
			 * takes three parameters. The first is going to be the principal. Well that's
			 * our username, which we called user. The second is the password and well,
			 * actually, let's put a null in here because we're not using passwords and then
			 * the third parameter is the credentials and that should be this array of
			 * granted authorities.
			 * 
			 */

			return new UsernamePasswordAuthenticationToken(user, null, roles);
		} catch (Exception e) {
			// token is not valid;
			return null;
		}
	}

}
