package vn.minhdat.jobhunter_be.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setTokenWithTTL(String key, String value, long ttl, TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    public boolean hasToken(String token) {
        return this.redisTemplate.hasKey(token);
    }

    public void deleteToken(String key) {
        this.redisTemplate.delete(key);
    }

    public void replaceToken(String oldToken, String newToken, String value, long ttl, TimeUnit timeUnit) {
        this.redisTemplate.delete("refresh:" + oldToken);
        this.redisTemplate.opsForValue().set("refresh:" + newToken, value, ttl, timeUnit);
    }
}
