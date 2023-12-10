package com.woowa.woowakit.global.config;

import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountMemoryRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountRepository;
import com.woowa.woowakit.infra.coupon.CouponDeployAmountCacheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private final String host;
    private final int port;

    public RedisConfig(
            @Value("${spring.redis.host}") final String host,
            @Value("${spring.redis.port}") final int port
    ) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public CouponDeployAmountRepository getCouponDeployAmountRepository() {
        return new CouponDeployAmountMemoryRepository();
    }

    @ConditionalOnProperty(name = "spring.redis.test", havingValue = "enable")
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @ConditionalOnProperty(name = "spring.redis.test", havingValue = "enable")
    @Bean
    @Primary
    public RedisTemplate<String, Integer> couponStore() {
        RedisTemplate<String, Integer> couponStore = new RedisTemplate<>();
        couponStore.setConnectionFactory(redisConnectionFactory());
        couponStore.setKeySerializer(new StringRedisSerializer());
        couponStore.setValueSerializer(new Jackson2JsonRedisSerializer<>(Integer.class));
        return couponStore;
    }

    @ConditionalOnProperty(name = "spring.redis.test", havingValue = "enable")
    @Bean
    @Primary
    public CouponDeployAmountRepository getCouponDeployAmountCacheRepository() {
        return new CouponDeployAmountCacheRepository(couponStore());
    }
}