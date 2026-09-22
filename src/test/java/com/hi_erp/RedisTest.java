package com.hi_erp;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
@Disabled("실제 Redis 서버가 필요한 통합 테스트 jenkins에선 제외")
public class RedisTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void redisTest() {
        String key = "name";
        String value = "Eunji";

        redisTemplate.opsForValue().set(key, value);
        String result = (String) redisTemplate.opsForValue().get(key);

        System.out.println("Redis에서 꺼내온 값: " + result);
    }
}
