package com.sparta.springauth.jwt;

import com.sparta.springauth.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override

    // doFilterInternal: 실제 필터링 작업을 수행하는 메서드입니다.
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        // jwtUtil.getTokenFromRequest(req): 요청에서 JWT 토큰을 추출합니다.
        String tokenValue = jwtUtil.getTokenFromRequest(req);


        if (StringUtils.hasText(tokenValue)) {
            // JWT 토큰 substring
            // jwtUtil.substringToken(tokenValue): 필요한 경우 JWT 토큰을 가공합니다.
            tokenValue = jwtUtil.substringToken(tokenValue);
            log.info(tokenValue);


            // jwtUtil.validateToken(tokenValue): JWT 토큰의 유효성을 검사합니다.
            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
                return;
            }


            // jwtUtil.getUserInfoFromToken(tokenValue): JWT 토큰에서 사용자 정보(Claims)를 추출합니다.
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {

                // setAuthentication(info.getSubject()): 추출된 사용자 이름을 기반으로 인증 처리를 수행합니다.
                // 이는 SecurityContext에 인증 객체를 설정하여 Spring Security에서 인증된 사용자로 처리할 수 있도록 합니다.
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리

    // setAuthentication: 주어진 사용자 이름을 기반으로 Spring Security의 SecurityContext에 인증 객체를 설정합니다.
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // createAuthentication(username): 주어진 사용자 이름을 이용하여 UserDetailsServiceImpl에서 사용자 정보를 조회하고 Authentication 객체를 생성합니다.
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성

    // createAuthentication: 주어진 사용자 이름을 기반으로 사용자 정보를 조회하고 UsernamePasswordAuthenticationToken을 생성하여 반환합니다.
    private Authentication createAuthentication(String username) {

        // userDetailsService.loadUserByUsername(username): 사용자 이름을 기반으로 사용자 정보를 조회합니다.
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}