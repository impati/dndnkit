package com.woowa.woowakit.domain.coupon.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponDeployAmountManager {

    private final CouponDeployAmountRepository couponDeployAmountCacheRepository;

    public void decreaseDeployAmount(final CouponGroup couponGroup) {
        if (!couponGroup.isLimitType()) {
            return;
        }
        long amount = couponDeployAmountCacheRepository.decrease(couponGroup);
        if (amount == 0) {
            couponGroup.shutDown();
        }
    }

    public void increase(final CouponGroup couponGroup) {
        couponDeployAmountCacheRepository.increase(couponGroup);
    }
}
