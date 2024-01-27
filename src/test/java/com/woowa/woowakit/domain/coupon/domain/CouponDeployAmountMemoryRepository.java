package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.ExhaustedCouponDeployAmountException;
import com.woowa.woowakit.domain.coupon.exception.NotFoundCouponDeployAmountException;
import java.util.HashMap;
import java.util.Map;

public class CouponDeployAmountMemoryRepository implements CouponDeployAmountRepository {

    private final Map<String, Integer> store = new HashMap<>();

    @Override
    public void deploy(final Long couponGroupId, final int CouponDeployAmount) {
        store.put(getKey(couponGroupId), CouponDeployAmount);
    }

    @Override
    public long decrease(final CouponGroup couponGroup) {
        if (!couponGroup.isLimitType()) {
            throw new IllegalStateException("쿠폰 그룹이 무제한 수량 발급인 경우 수량을 적재하거나 감소시킬 수 없습니다.");
        }
        Long decrement = decrement(couponGroup);
        if (decrement < 0) {
            throw new ExhaustedCouponDeployAmountException();
        }

        return decrement;
    }

    @Override
    public void increase(final CouponGroup couponGroup) {
        if (!couponGroup.isLimitType()) {
            return;
        }
        store.put(getKey(couponGroup.getId()), store.get(getKey(couponGroup.getId())) + 1);
    }

    private synchronized Long decrement(final CouponGroup couponGroup) {
        store.put(getKey(couponGroup.getId()), store.get(getKey(couponGroup.getId())) - 1);
        return (long) store.get(getKey(couponGroup.getId()));
    }

    @Override
    public int getCouponDeployAmount(final Long couponGroupId) {
        Integer couponDeployAmount = store.get(getKey(couponGroupId));
        if (couponDeployAmount == null) {
            throw new NotFoundCouponDeployAmountException();
        }
        return couponDeployAmount;
    }

    @Override
    public void clear(final Long couponGroupId) {
        store.remove(getKey(couponGroupId));
    }

    private String getKey(final Long couponGroupId) {
        return "COUPON_GROUP : " + couponGroupId;
    }
}
