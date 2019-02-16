package com.guimu.alpha.utils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component("RedisUtils")
public class RedisUtils {

    @Autowired
    @Qualifier("redisTrafficTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    JedisConnectionFactory jedisConnectionFactory;

    /**
     * set
     *
     * @param redisKey key
     * @param object value
     * @param seconds 超时时间
     */
    public void set(String redisKey, String object, long seconds) {
        if (object != null) {
            if (seconds != -1) {
                redisTemplate.opsForValue().set(redisKey, object, seconds, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(redisKey, object);
            }
        }
    }

    /**
     * get方法
     *
     * @param redisKey redisKey
     */

    public <T> T get(String redisKey) {
        Object object = redisTemplate.opsForValue().get(redisKey);
        return (T) object;
    }

    public Set<String> getPatternKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * @Description: 批量获取key-value
     * @Param: [keys]
     * @Return: java.util.List<java.lang.String>
     * @Author: Guimu
     * @Date: 2018/7/30  下午2:38
     */
    public List<String> batchGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public void clearAll() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return null;
        });
    }


    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }


}
