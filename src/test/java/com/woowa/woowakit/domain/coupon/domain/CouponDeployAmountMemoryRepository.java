package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.NotFoundCouponDeployAmountException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CouponDeployAmountMemoryRepository implements CouponDeployAmountRepository {

    private final Map<String, Integer> store = new ConcurrentHashMap<>();

    @Override
    public void deploy(final Long couponGroupId, final int CouponDeployAmount) {
        store.put(String.valueOf(couponGroupId), CouponDeployAmount);
    }

    @Override
    public int getCouponDeployAmount(final Long couponGroupId) {
        Integer couponDeployAmount = store.get(String.valueOf(couponGroupId));
        if (couponDeployAmount == null) {
            throw new NotFoundCouponDeployAmountException();
        }

        return couponDeployAmount;
    }

    @Override
    public void clear(final Long couponGroupId) {
        store.clear();
    }
}
