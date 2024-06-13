package com.sparta.springauth.controller;

import com.sparta.springauth.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// api/user 를 인증이 잘 되는지 확인하기 위한 클래스

/*
Spring Boot 애플리케이션에서 인증이 잘 이루어지는지 확인하기 위한 간단한 컨트롤러 클래스.
클래스는 /api/products 경로에 대한 GET 요청을 처리하며, 요청이 인증되었는지 확인하고 로그에 사용자 정보를 출력하는 기능


이 컨트롤러는 JWT 인증이 제대로 동작하는지 확인하는 데 사용됩니다.
JWT 인증 필터가 제대로 설정되어 있을 때, user 객체가 요청 속성에 설정되고, 사용자 정보를 콘솔에 출력하게 됩니다.
 */


@Controller // 이 클래스가 Spring MVC의 컨트롤러임을 나타냅니다.
@RequestMapping("/api") // 이 클래스의 모든 요청 경로 앞에 /api가 추가됩니다. 즉, 이 컨트롤러의 모든 메서드는 /api로 시작하는 URL을 처리합니다.
public class ProductController {

    @GetMapping("/products") // 이 메서드는 HTTP GET 요청을 처리하며, /api/products 경로에 매핑됩니다.
    public String getProducts(HttpServletRequest req) { // HttpServletRequest 객체를 인자로 받아 요청 정보를 사용합니다. 반환 타입은 String입니다.
        System.out.println("ProductController.getProducts : 인증 완료"); // 이를 통해 요청이 이 메서드로 들어왔는지 확인할 수 있습니다.
        User user = (User) req.getAttribute("user"); //  user 객체를 가져옵니다. 이 user 객체는 인증 필터에서 설정된 사용자 정보입니다. 만약 인증이 제대로 되었다면, user 객체는 요청에 설정되어 있을 것
        System.out.println("user.getUsername() = " + user.getUsername()); // 가져온 user 객체의 사용자 이름을 콘솔에 출력합니다. 이를 통해 인증이 성공했으며, 해당 사용자의 정보를 확인

        return "redirect:/"; // 요청을 처리한 후 / 경로로 사용자를 이동시킵니다.
    }
}