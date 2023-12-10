package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.ExhaustedCouponDeployAmountException;
import com.woowa.woowakit.domain.coupon.exception.IssueCouponException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssuableDecider {

    private final CouponDeployAmountRepository couponDeployAmountRepository;

    public void validateIssuable(final CouponGroup couponGroup) {
        if (couponGroup.isLimitType()) {
            validate(couponGroup);
        }
    }

    private void validate(final CouponGroup couponGroup) {
        if (couponDeployAmountRepository.getCouponDeployAmount(couponGroup.getId()) <= 0) {
            throw new ExhaustedCouponDeployAmountException();
        }
        if (!couponGroup.isDeployStatus()) {
            throw new IssueCouponException();
        }
    }
}
