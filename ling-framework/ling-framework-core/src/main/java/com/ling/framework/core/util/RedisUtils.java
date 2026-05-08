package com.ling.framework.core.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static cn.hutool.extra.spring.SpringUtil.getBean;

@UtilityClass
@SuppressWarnings("unchecked")
public class RedisUtils {

    private static final RedisTemplate<String,Object> redisTemplate;

    static {
        redisTemplate = getBean(RedisTemplate.class);
    }

    // ============================ Key 操作 ============================

    /**
     * 判断 key 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除 key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除 key
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 设置 key 的过期时间（秒）
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取 key 的剩余过期时间（秒）
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    // ============================ String 操作 ============================

    /**
     * 设置值（永不过期）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值并指定过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 递增（原子操作）
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ============================ Hash 操作 ============================

    /**
     * 向 hash 中放入一个字段
     */
    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 批量设置 hash 字段
     */
    public void hsetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取 hash 中的某个字段
     */
    public <T> T hget(String key, String field) {
        return (T) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取整个 hash
     */

    public <T> Map<String, T> hgetAll(String key) {

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        return (Map<String, T>) (Map<?, ?>) entries;
    }

    /**
     * 删除 hash 中的一个或多个字段
     */
    public Long hdel(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 判断 hash 中是否存在某字段
     */
    public Boolean hexists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ============================ List 操作 ============================

    /**
     * 向 list 左侧插入元素
     */
    public void lpush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 向 list 右侧插入元素
     */
    public void rpush(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 获取 list 指定范围的元素 [start, end]
     */
    public <T> List<T> lrange(String key, long start, long end) {
        return (List<T>) redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取 list 长度
     */
    public Long llen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 移除并返回 list 左侧第一个元素
     */
    public <T> T lpop(String key) {
        return (T) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移除并返回 list 右侧第一个元素
     */
    public <T> T rpop(String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    // ============================ Set 操作 ============================

    /**
     * 向 set 添加元素
     */
    public void sadd(String key, Object... values) {
        redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取 set 所有元素
     */
    public <T> Set<T> smembers(String key) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }

    /**
     * 判断是否是 set 成员
     */
    public Boolean sismember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 删除 set 中的元素
     */
    public Long srem(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取 set 大小
     */
    public Long scard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    // ============================ ZSet（有序集合）============================

    /**
     * 添加元素到 zset（带分数）
     */
    public void zadd(String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取 zset 排名区间内的元素（按分数升序）
     */
    public <T> Set<T> zrangeByScore(String key, double min, double max) {
        return (Set<T>) redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取元素的分数
     */
    public Double zscore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 删除 zset 中的元素
     */
    public Long zrem(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 获取 zset 大小
     */
    public Long zcard(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
}
