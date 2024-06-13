package com.sparta.springauth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper; // : JSON 데이터를 Java 객체로 매핑하는 Jackson 라이브러리의 클래스입니다.
import com.sparta.springauth.dto.LoginRequestDto; //  로그인 요청 DTO 객체입니다.
import com.sparta.springauth.entity.UserRoleEnum; // 사용자 역할을 나타내는 열거형입니다.
import com.sparta.springauth.security.UserDetailsImpl; // 사용자 정보를 담은 UserDetails 구현 클래스입니다.
import jakarta.servlet.FilterChain; // Servlet 관련 클래스들입니다.
import jakarta.servlet.ServletException; //
import jakarta.servlet.http.HttpServletRequest; //
import jakarta.servlet.http.HttpServletResponse; //
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Spring Security에서 제공하는 기본 로그인 인증 필터입니다.

import java.io.IOException;


/*
 JWT(JSON Web Token) 기반의 사용자 인증을 처리하기 위한 필터인 JwtAuthenticationFilter 클래스입니다.
 Spring Security의 UsernamePasswordAuthenticationFilter를 확장하여 사용자의 로그인 요청을 처리하고,
 로그인 성공 시 JWT를 생성하고 설정된 위치에 저장하는 역할을 합니다.
 */


@Slf4j(topic = "로그인 및 JWT 생성") // 로그를 기록하기 위한 SLF4J 어노테이션입니다.
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter { // UsernamePasswordAuthenticationFilter를 상속받아 JWT 인증을 처리하는 필터입니다.
    private final JwtUtil jwtUtil; //  JWT 토큰 생성 및 관리를 위한 유틸리티 클래스 객체입니다.

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/user/login"); // 로그인 처리 URL을 설정합니다.
    }

    @Override

    // 사용자의 로그인 시도를 처리하는 메서드
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class); // 요청된 JSON 데이터를 LoginRequestDto 객체로 매핑합니다.



            // Spring Security의 인증 매니저를 통해 사용자 인증을 시도합니다.
            // 여기서는 사용자 이름과 비밀번호로 UsernamePasswordAuthenticationToken 객체를 생성하여 반환합니다.
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override

    // 로그인이 성공했을 때 호출되며, JWT를 생성하고 HTTP 응답에 쿠키로 추가하는 메서드입니다.
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");

        // authResult.getPrincipal(): 인증 결과에서 Principal(주체)을 가져와서 UserDetailsImpl로 캐스팅하여 사용자 이름과 역할을 추출합니다.
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();


        // jwtUtil.createToken(username, role): 사용자 이름과 역할을 기반으로 JWT 토큰을 생성합니다.
        String token = jwtUtil.createToken(username, role);
        jwtUtil.addJwtToCookie(token, response); // 생성된 JWT 토큰을 HTTP 응답의 쿠키에 추가하여 클라이언트에게 전달합니다.
    }

    @Override

    // unsuccessfulAuthentication: 로그인이 실패했을 때 호출되며, HTTP 응답 상태를 401 Unauthorized로 설정합니다.
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}