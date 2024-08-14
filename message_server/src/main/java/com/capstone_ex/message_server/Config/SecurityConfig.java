package com.capstone_ex.message_server.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/**").permitAll()   // 모든 요청에 대해 접근 허용 (실제 배포 시에는 더 세분화된 권한 설정 필요)
                                .anyRequest().authenticated()         // 나머지 요청은 인증된 사용자만 접근 가능
                )
                .formLogin(withDefaults()) // 로그인 설정
                .logout(withDefaults())   // 로그아웃 설정
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .cors(withDefaults()); // CORS 설정
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOriginPatterns(Collections.singletonList("*")); // 모든 도메인 허용
        configuration.setAllowedOrigins(Arrays.asList("http://192.168.0.6:3000", "http://localhost:3000")); // 프론트엔드 주소
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용된 HTTP 메소드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 허용된 헤더
        configuration.setAllowCredentials(true); // 자격 증명을 포함한 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
