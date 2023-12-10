package com.woowa.woowakit.domain.coupon.domain;

public interface CouponDeployAmountRepository {

    void deploy(final Long couponGroupId, final int CouponDeployAmount);

    void decrease(final CouponGroup couponGroup);

    int getCouponDeployAmount(final Long couponGroupId);

    void clear(final Long couponGroupId);
}
