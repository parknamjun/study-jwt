package kr.giljabi.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =  new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);   //서버 응답시 json을 javascript에서 처리할수 있도록 설정, false는 javascript에서 사용불가
        config.addAllowedOrigin("*");   //모든 IP응답 허용
        config.addAllowedHeader("*");   //모든 header 응답 허용
        config.addAllowedMethod("*");   //모든 post, get, put, delete, patch 요청 허용
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
