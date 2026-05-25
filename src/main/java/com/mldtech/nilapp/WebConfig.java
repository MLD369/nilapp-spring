package com.mldtech.nilapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(
//                                "http://localhost:4200",
//                                "https://api.mldtechnology.com",
//                                "https://mldtechnology.com",
//                                "http://mldtechnology.com",
//                                "http://www.mldtechnology.com",
//                                "https://www.mldtechnology.com"
                        )
                        .allowedMethods("GET", "POST", "OPTIONS","PUT")// "PUT", "DELETE"
                        .allowedHeaders("*");
            }
        };
    }
}

