package com.toms.order.center.redis;

import javax.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public String setGet(String value) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        opsForValue.set("test_key", value);
        return (String) opsForValue.get("test_key");
    }
}
