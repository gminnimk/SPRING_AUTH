package com.sparta.springauth.food;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component

// 범용적으로 사용되는 Bean 객체에는 Primary를!
@Primary // 같은 타입의 Bean이 여러개 있을 때 범용적으로 사용되는 Bean 객체에 사용 , 큰 범위
public class Chicken implements Food {
    @Override
    public void eat() {
        System.out.println("치킨을 먹습니다.");
    }
}