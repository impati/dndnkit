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
    public void decrease(final CouponGroup couponGroup) {
        Long decrement = decrement(couponGroup);
        if (decrement < 0) {
            throw new ExhaustedCouponDeployAmountException();
        }
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
