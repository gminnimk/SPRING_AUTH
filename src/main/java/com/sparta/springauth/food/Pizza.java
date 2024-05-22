package com.sparta.springauth.food;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component

// Spring 에서는 설정에 우선순위를 논할때
// 큰 범위(Primary)가 우선순위가 더 낮고 좁은 범위(Qualfier)가 더 높음


// 지엽적으로 사용되는 Bean 객체에는 Qualfier를!
@Qualifier("pizza")
public class Pizza implements Food {
    @Override
    public void eat() {
        System.out.println("피자를 먹습니다.");
    }
}