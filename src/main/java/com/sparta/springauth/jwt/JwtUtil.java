package com.sparta.springauth.jwt;

import com.sparta.springauth.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component


// jwt 관련된 기능들을 가진 클래스
public class JwtUtil {


    /*JWT 데이터*/
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer "; // 한 칸 띄는거 기억
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }




    /* 토큰 생성 = JWT 생성 */
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }




    /* JWT Cookie 에 저장 */
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }

    /* Cookie에 들어있던 JWT 토큰을 substring  */
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    /* JWT 토큰 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }


    /* JWT 토큰에서 사용자 정보 가져오기 */
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }


    // HttpServletRequest 에서 Cookie Value : JWT 가져오기

    /*
    이 메서드는 HTTP 요청에서 쿠키 배열을 가져와, AUTHORIZATION_HEADER 이름을 가진 쿠키를 찾고, 해당 쿠키의 값을 URL 디코딩하여 반환합니다.
    쿠키가 없거나 JWT 쿠키를 찾지 못하면 null을 반환합니다. 이를 통해 JWT 토큰을 요청의 쿠키에서 추출할 수 있습니다.
     */
    public String getTokenFromRequest(HttpServletRequest req) {
        //  HttpServletRequest 객체를 인자로 받아, 요청에 포함된 쿠키에서 JWT 토큰을 추출하여 반환합니다. 반환 타입은 String
        Cookie[] cookies = req.getCookies(); // 요청에서 모든 쿠키를 배열 형태로 가져옵니다. 쿠키가 없으면 null을 반환합니다.
        if(cookies != null) { // 쿠키 배열이 null이 아닌지 확인합니다. 쿠키가 존재할 경우에만 다음 작업을 진행합니다.
            for (Cookie cookie : cookies) { // 각 쿠키를 순회하며, 쿠키의 이름이 AUTHORIZATION_HEADER와 같은지 확인합니다
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) { // AUTHORIZATION_HEADER는 JWT 토큰이 저장된 쿠키의 이름을 나타냅니다

                    // 쿠키 값은 URL 인코딩되어 있을 수 있으므로, URLDecoder.decode 메서드를 사용해 디코딩
                    // 디코딩 과정에서 UnsupportedEncodingException이 발생할 수 있으며, 이 경우 null을 반환합니다.
                    try {
                        // Encode 되어 넘어간 Value 다시 Decode ,해당 쿠키를 찾으면 cookie.getValue()로 쿠키 값을 가져옵니다.
                        return URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }


}