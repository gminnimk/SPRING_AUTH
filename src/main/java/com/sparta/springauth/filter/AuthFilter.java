package com.sparta.springauth.filter;

import com.sparta.springauth.entity.User;
import com.sparta.springauth.jwt.JwtUtil;
import com.sparta.springauth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;


//  Spring Boot 애플리케이션에서 JWT를 이용한 인증 필터를 구현한 것입니다. 이를 통해 특정 요청에 대해 인증 절차를 진행
/*
이 클래스는 특정 URL에 대해 인증 절차를 수행하는 JWT 인증 필터입니다.
예외 처리된 URL을 제외한 모든 요청에 대해 JWT 토큰을 확인하고 검증하며, 토큰이 유효한 경우 사용자 정보를 가져와 요청에 추가합니다.
@Order(2)로 설정되어 있어, 다른 필터들보다 나중에 실행됩니다.
 */


@Slf4j(topic = "AuthFilter") // 로깅을 위한 설정으로, AuthFilter라는 이름의 로거를 생성합니다.
@Component // Spring에서 이 클래스를 빈으로 등록합니다.
@Order(2) // 이 필터의 실행 순서를 지정합니다. 숫자가 낮을수록 먼저 실행됩니다.
public class AuthFilter implements Filter { // 이 클래스가 Filter 인터페이스를 구현한다고 선언합니다.

    private final UserRepository userRepository; // UserRepository는 사용자 정보를 데이터베이스에서 가져오는 데 사용
    private final JwtUtil jwtUtil; // JwtUtil은 JWT 토큰 관련 유틸리티 클래스입니다.

    public AuthFilter(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override

    // doFilter 메서드는 필터의 핵심 기능을 정의합니다. 요청이 필터를 통과할 때마다 이 메서드가 호출됩니다.
    // HttpServletRequest로 요청을 캐스팅하고, 요청된 URL을 가져옵니다
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = httpServletRequest.getRequestURI();

        if (StringUtils.hasText(url) &&
                (url.startsWith("/api/user") || url.startsWith("/css") || url.startsWith("/js"))
        ) {
            // 회원가입, 로그인 관련 API 는 인증 필요없이 요청 진행
            // /api/user, /css, /js로 시작하는 URL은 인증 없이 요청을 진행합니다.
            chain.doFilter(request, response); // 다음 Filter 로 이동
        } else {
            // 나머지 API 요청은 인증 처리 진행
            // 토큰 확인
            String tokenValue = jwtUtil.getTokenFromRequest(httpServletRequest); // jwtUtil.getTokenFromRequest(httpServletRequest): 요청에서 토큰을 추출합니다.

            if (StringUtils.hasText(tokenValue)) { // 토큰이 존재하면 검증 시작
                // JWT 토큰 substring
                String token = jwtUtil.substringToken(tokenValue); // 토큰이 존재하면, jwtUtil.substringToken(tokenValue): 토큰에서 Bearer 접두사를 제거합니다.

                // 토큰 검증
                if (!jwtUtil.validateToken(token)) { // 토큰을 검증합니다. 유효하지 않으면 예외를 던집니다.
                    throw new IllegalArgumentException("Token Error");
                }

                // 토큰에서 사용자 정보 가져오기
                Claims info = jwtUtil.getUserInfoFromToken(token); // jwtUtil.getUserInfoFromToken(token): 토큰에서 사용자 정보를 가져옵니다.

                User user = userRepository.findByUsername(info.getSubject()).orElseThrow(() ->
                        // userRepository.findByUsername(info.getSubject()): 사용자 정보를 데이터베이스에서 조회합니다. 사용자가 없으면 예외를 던집니다.
                        new NullPointerException("Not Found User")
                );

                request.setAttribute("user", user);
                chain.doFilter(request, response); // 다음 Filter 로 이동
            } else {
                throw new IllegalArgumentException("Not Found Token");
            }
        }
    }

}