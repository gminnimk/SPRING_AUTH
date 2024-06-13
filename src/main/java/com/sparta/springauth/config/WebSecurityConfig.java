package com.sparta.springauth.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*
Spring Boot 애플리케이션에서 Spring Security를 설정하는 구성 클래스입니다.
이 클래스는 HTTP 요청에 대한 보안 설정을 정의하며, 특정 경로에 대한 접근을 허용하거나 인증을 요구하고, 사용자 인증을 처리하는 방식을 지정합니다.
 */


@Configuration // 이 클래스가 Spring 설정 클래스임을 나타냅니다.
@EnableWebSecurity // Spring Security 지원을 가능하게 함 ,  Spring Security를 활성화하여 보안 기능을 사용할 수 있도록 합니다.

public class WebSecurityConfig {

    @Bean // 이 메서드가 Spring 컨텍스트에서 관리하는 빈을 생성함을 나타냅니다.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //보안 필터 체인을 정의하는 메서드입니다. HttpSecurity를 인자로 받아 보안 설정을 구성합니다.

        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());
        /*
        CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화합니다.
        일반적으로 API 서버에서는 CSRF 보호가 필요하지 않기 때문에 이를 비활성화할 수 있습니다.
         */

        http.authorizeHttpRequests((authorizeHttpRequests) -> // authorizeHttpRequests: HTTP 요청에 대한 인가 설정을 구성합니다.
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        // 정적 리소스(예: CSS, JavaScript, 이미지)에 대한 요청은 인증 없이 접근을 허용합니다.
                        .requestMatchers("/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                        // 인증 없이 접근을 허용!

                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 로그인 사용
        http.formLogin((formLogin) -> // 폼 로그인을 설정합니다.
                formLogin
                        // 로그인 View 제공 (GET /api/user/login-page)
                        .loginPage("/api/user/login-page") // 사용자 로그인 페이지를 제공하는 URL을 설정합니다. (GET 요청)
                        // 로그인 처리 (POST /api/user/login)
                        .loginProcessingUrl("/api/user/login") //  로그인 처리 요청을 처리할 URL을 설정합니다. (POST 요청)
                        // 로그인 처리 후 성공 시 URL
                        .defaultSuccessUrl("/") // 로그인 성공 시 이동할 기본 URL을 설정합니다.
                        // 로그인 처리 후 실패 시 URL
                        .failureUrl("/api/user/login-page?error") // 로그인 실패 시 이동할 URL을 설정합니다.
                        .permitAll() // 로그인 관련 요청은 인증 없이 접근을 허용합니다.
        );

        return http.build(); //  구성된 보안 필터 체인을 반환합니다. 이 필터 체인이 애플리케이션의 보안 설정을 담당합니다.
    }
}