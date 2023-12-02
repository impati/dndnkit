package com.woowa.woowakit.infra.coupon;

import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountRepository;
import com.woowa.woowakit.domain.coupon.exception.NotFoundCouponDeployAmountException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public class CouponDeployAmountCacheRepository implements CouponDeployAmountRepository {

    private static final String COUPON_GROUP_KEY_NAME = "[DEPLOY] COUPON_GROUP_ID : ";

    private final RedisTemplate<String, Integer> redisTemplate;

    @Override
    public void deploy(final Long couponGroupId, final int CouponDeployAmount) {
        redisTemplate.opsForValue().set(getKey(couponGroupId), CouponDeployAmount);
    }

    @Override
    public int getCouponDeployAmount(final Long couponGroupId) {
        Integer deployAmount = redisTemplate.opsForValue().get(getKey(couponGroupId));
        if (deployAmount == null) {
            throw new NotFoundCouponDeployAmountException();
        }

        return deployAmount;
    }

    @Override
    public void clear(final Long couponGroupId) {
        redisTemplate.opsForValue().getAndDelete(getKey(couponGroupId));
    }

    private String getKey(final Long couponGroupId) {
        return COUPON_GROUP_KEY_NAME + couponGroupId;
    }
}
