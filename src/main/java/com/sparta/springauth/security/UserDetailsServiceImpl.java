package com.sparta.springauth.security;

import com.sparta.springauth.entity.User; // 사용자 엔티티를 정의한 클래스
import com.sparta.springauth.repository.UserRepository; // 사용자 엔티티를 조회하기 위한 리포지토리 인터페이스
import org.springframework.security.core.userdetails.UserDetails; //  Spring Security에서 사용자 인증과 관련된 클래스 및 인터페이스
import org.springframework.security.core.userdetails.UserDetailsService; // 위와 동일
import org.springframework.security.core.userdetails.UsernameNotFoundException; // 위와 동일
import org.springframework.stereotype.Service; // 빈으로 등록되는 서비스 클래스임을 나타냅니다.

/*
 Spring Security에서 사용자 인증을 처리하기 위해 UserDetailsService 인터페이스를 구현한 UserDetailsServiceImpl 클래스입니다.
 클래스는 주어진 사용자 이름(사용자 아이디)을 기반으로 데이터베이스에서 사용자를 조회하고,
 조회된 사용자 정보를 UserDetails 인터페이스를 구현한 UserDetailsImpl 객체로 변환하여 반환합니다.
 */

@Service // Spring에 의해 빈으로 관리되는 서비스 클래스
public class UserDetailsServiceImpl implements UserDetailsService { // 인터페이스를 구현하는 UserDetailsServiceImpl 클래스

    private final UserRepository userRepository; // 사용자 정보를 조회할 때 사용할 UserRepository 인스턴스

    public UserDetailsServiceImpl(UserRepository userRepository) { // UserRepository를 주입받아 필드에 할당
        this.userRepository = userRepository;
    }

    @Override
    // 주어진 사용자 이름(사용자 아이디)을 기반으로 사용자 정보를 로드하는 메서드입니다.
    // 이 메서드는 UserDetailsService 인터페이스에서 정의된 메서드
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username) // UserRepository를 사용하여 데이터베이스에서 주어진 사용자 이름으로 사용자 정보를 조회합니다.
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username)); // 사용자가 존재하지 않으면 UsernameNotFoundException을 발생시킵니다.

        return new UserDetailsImpl(user);
        // 조회된 사용자 정보를 UserDetailsImpl 객체로 변환하여 반환합니다. UserDetailsImpl은 UserDetails 인터페이스를 구현한 클래스로,
        // Spring Security가 사용자 인증 및 인가를 처리하는 데 필요한 사용자 정보를 제공합니다.
    }
}