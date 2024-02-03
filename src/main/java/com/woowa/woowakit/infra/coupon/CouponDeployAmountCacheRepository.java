package com.woowa.woowakit.infra.coupon;

import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.exception.ExhaustedCouponDeployAmountException;
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
    public long decrease(final CouponGroup couponGroup) {
        if (!couponGroup.isLimitType()) {
            throw new IllegalStateException("쿠폰 그룹이 무제한 수량 발급인 경우 수량을 적재하거나 감소시킬 수 없습니다.");
        }
        long amount = redisTemplate.opsForValue().decrement(getKey(couponGroup.getId()));
        if (amount < 0) {
            throw new ExhaustedCouponDeployAmountException();
        }
        return amount;
    }

    @Override
    public void increase(final CouponGroup couponGroup) {
        redisTemplate.opsForValue().increment(getKey(couponGroup.getId()));
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
