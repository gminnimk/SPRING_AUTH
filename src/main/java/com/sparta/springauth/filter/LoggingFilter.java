package com.sparta.springauth.filter;

//  Spring Boot 애플리케이션에서 HTTP 요청과 응답을 가로채서 로그를 남기는 "로깅 필터"

/*
이 클래스는 HTTP 요청이 들어올 때마다 해당 요청의 URL을 로그로 남기고, 비즈니스 로직이 완료된 후에도 로그를 남깁니다.
이를 통해 요청과 응답의 흐름을 추적할 수 있습니다. @Order(1)로 설정되어 있어, 다른 필터들보다 우선 실행됩니다.
 */


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j(topic = "LoggingFilter") // 로깅을 위한 설정으로, LoggingFilter라는 이름의 로거를 생성합니다.
@Component //  Spring에서 이 클래스를 빈으로 등록합니다.
@Order(1) // 이 필터의 실행 순서를 지정합니다. 숫자가 낮을수록 먼저 실행됩니다.
public class LoggingFilter implements Filter { // 이 클래스가 Filter 인터페이스를 구현한다고 선언합니다.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // doFilter 메서드는 필터의 핵심 기능을 정의합니다. 요청이 필터를 통과할 때마다 이 메서드가 호출
        // 전처리
        HttpServletRequest httpServletRequest = (HttpServletRequest) request; // ServletRequest를 HttpServletRequest로 캐스팅합니다. 이렇게 하면 HTTP 관련 메서드를 사용할 수 있습니다.
        String url = httpServletRequest.getRequestURI(); // 요청된 URL을 가져옵니다.
        log.info(url); // URL을 로그에 남깁니다.

        chain.doFilter(request, response); // 다음 필터로 요청과 응답을 전달합니다. 만약 이 필터가 마지막 필터라면, 실제 서블릿이 호출

        // 후처리
        log.info("비즈니스 로직 완료"); // 비즈니스 로직이 완료된 후 로그를 남깁니다.
    }
}