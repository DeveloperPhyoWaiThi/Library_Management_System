package com.sample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/GIC-T-06-User/Documents/Library-Management-System-Zip-(10.7.24)/Library-Management-System-Zip/Library-Management-System/uploads/");
        
        registry.addResourceHandler("/profiles/**")
        .addResourceLocations("file:C:/Users/GIC-T-06-User/Documents/Library-Management-System-Zip-(10.7.24)/Library-Management-System-Zip/Library-Management-System/profiles/");
    }
	
	
}
