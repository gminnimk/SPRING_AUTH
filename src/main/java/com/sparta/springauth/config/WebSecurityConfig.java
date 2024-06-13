package com.sparta.springauth.config;

import com.sparta.springauth.jwt.JwtAuthorizationFilter;
import com.sparta.springauth.jwt.JwtAuthenticationFilter;
import com.sparta.springauth.jwt.JwtUtil;
import com.sparta.springauth.security.UserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



/*
 Spring Security를 설정하여 JWT 기반의 인증 및 인가를 처리하는 방법을 보여줍니다.
 필요한 빈들을 등록하고, HTTP 요청에 대한 인가 설정을 하며, 필터를 추가하여 JWT 인증 처리 및 인가를 관리합니다.
 이를 통해 보안적으로 더 안전한 웹 애플리케이션을 구성할 수 있습니다.
 */
@Configuration // 이 클래스가 Spring의 Java 설정 클래스임을 나타냅니다.
@EnableWebSecurity // Spring Security 지원을 가능하게 함 , Spring Security를 사용할 수 있도록 활성화합니다.
public class WebSecurityConfig { // Spring Security 설정을 담당하는 클래스입니다. 생성자에서 필요한 빈들을 주입받습니다.

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean

    // AuthenticationManager를 빈으로 등록합니다. 이는 Spring Security 인증을 관리하는 데 필요합니다.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean

    // JwtAuthenticationFilter를 빈으로 등록합니다. 이 필터는 사용자의 로그인을 처리하고 JWT를 생성합니다.
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean

    // JwtAuthorizationFilter를 빈으로 등록합니다. 이 필터는 JWT를 검증하고 사용자를 인증합니다.
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean

    // securityFilterChain을 빈으로 등록하여 Spring Security의 설정을 구성합니다.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        // http.csrf((csrf) -> csrf.disable()): CSRF 보호 기능을 비활성화합니다.
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        // http.sessionManagement((sessionManagement) ->
        // sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)): 세션 관리 방식을 STATELESS로 설정하여 세션을 사용하지 않도록 합니다.
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );


        // http.authorizeHttpRequests((authorizeHttpRequests) -> { ... }): 요청에 대한 인가 설정을 구성합니다. 정적 리소스는 인증 없이 접근할 수 있도록 하고,
        // '/api/user/'로 시작하는 요청도 인증 없이 접근할 수 있도록 합니다.
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );



        // http.formLogin((formLogin) -> formLogin.loginPage("/api/user/login-page").permitAll()): 로그인 페이지를 설정하고,
        // 해당 페이지는 모든 사용자에게 접근 가능하도록 허용합니다.
        http.formLogin((formLogin) ->
                formLogin
                        .loginPage("/api/user/login-page").permitAll()
        );



        // 필터 관리

        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class); // JwtAuthorizationFilter를 JwtAuthenticationFilter 앞에 등록하여 JWT 검증 및 사용자 인증을 처리합니다.
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 등록하여 사용자의 로그인 요청을 처리하고 JWT를 생성합니다.

        return http.build();
    }
}