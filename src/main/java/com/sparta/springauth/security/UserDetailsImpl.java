package com.sparta.springauth.security;

import com.sparta.springauth.entity.User; // 사용자 엔티티와 역할을 정의한 클래스
import com.sparta.springauth.entity.UserRoleEnum; // 사용자 엔티티와 역할을 정의한 클래스
import org.springframework.security.core.GrantedAuthority; //  Spring Security에서 사용자의 권한을 나타내는 인터페이스와 클래스
import org.springframework.security.core.authority.SimpleGrantedAuthority; //  Spring Security에서 사용자의 권한을 나타내는 인터페이스와 클래스
import org.springframework.security.core.userdetails.UserDetails; // Spring Security에서 사용자 정보를 나타내는 인터페이스

import java.util.ArrayList;
import java.util.Collection;

/*
 Spring Security에서 사용자 인증 및 인가를 처리하기 위해 사용하는 UserDetails 인터페이스의 구현 클래스입니다.
 이 클래스는 사용자 정보를 캡슐화하여 Spring Security가 사용자를 인증하고 인가할 수 있도록 합니다.
 */

public class UserDetailsImpl implements UserDetails {

    private final User user; // User 객체를 저장하는 필드

    public UserDetailsImpl(User user) { // User 객체를 받아서 필드에 저장
        this.user = user;
    }

    public User getUser() { // 저장된 User 객체를 반환하는 메서드
        return user;
    }

    @Override
    public String getPassword() { //사용자 비밀번호를 반환
        return user.getPassword();
    }

    @Override
    public String getUsername() { // 사용자 이름(아이디)를 반환
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // getAuthorities(): 사용자의 권한(역할)을 반환
        UserRoleEnum role = user.getRole(); // 사용자 역할을 가져옵니다.
        String authority = role.getAuthority(); // 역할에 해당하는 권한 문자열을 가져옵니다.

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        // 권한 문자열을 SimpleGrantedAuthority 객체로 감쌉니다.

        Collection<GrantedAuthority> authorities = new ArrayList<>(); // 권한을 저장할 컬렉션을 생성
        authorities.add(simpleGrantedAuthority); // 컬렉션에 권한을 추가

        return authorities; // 권한 컬렉션을 반환
    }

    @Override

    // 계정이 만료되지 않았는지 여부를 반환합니다.
    // 여기서는 항상 true를 반환하여 계정이 만료되지 않았음을 나타냅니다.
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override

    // 계정이 잠기지 않았는지 여부를 반환합니다.
    // 여기서는 항상 true를 반환하여 계정이 잠기지 않았음을 나타냅니다.
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    // 자격 증명이 만료되지 않았는지 여부를 반환합니다.
    // 여기서는 항상 true를 반환하여 자격 증명이 만료되지 않았음을 나타냅니다.
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // 계정이 활성화되었는지 여부를 반환합니다.
    // 여기서는 항상 true를 반환하여 계정이 활성화되었음을 나타냅니다.
    public boolean isEnabled() {
        return true;
    }
}