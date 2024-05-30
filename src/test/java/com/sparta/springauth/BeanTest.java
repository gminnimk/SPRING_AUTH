package com.sparta.springauth;


import com.sparta.springauth.food.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // 지정해줘야 Bean 주입받는 di사용 가능
public class BeanTest {

    @Autowired // Bean 타입으로 지원을 하는데 연결이 되지 않을 경우 Bean 이름으로 찾는다
    @Qualifier("pizza") // 같은 타입의 Bean이 여러개 있을 때 지엽적으로 사용되는 Bean 객체에 사용
    Food food;

    @Test
    @DisplayName("Primary 와 Qualfier 우선순위 확인")
    void test1() {
        food.eat();
    }
}
