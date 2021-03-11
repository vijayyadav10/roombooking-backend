package com.vizz.roombooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
		.allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
		.allowedHeaders("*")
		.allowedOrigins("http://localhost:4200")
		.allowCredentials(true);
		
		//.allowedOrigins("*")
	//TODO: need to change the URL for the production URL when we deploy
	}
}
