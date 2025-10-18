package com.iuh.printshop.printshop_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Tắt CSRF để Postman có thể gọi POST/PUT/DELETE
                .csrf(csrf -> csrf.disable())

                // Cho phép tất cả request mà không cần đăng nhập
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                //  Không dùng session / form login mặc định
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
