package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.CouponIssueTypeException;
import java.util.List;

public class CouponHistory {

    private final CouponGroup couponGroup;
    private final List<Coupon> coupons;

    private CouponHistory(final CouponGroup couponGroup, final List<Coupon> coupons) {
        this.couponGroup = couponGroup;
        this.coupons = coupons;
    }

    public static CouponHistory of(final CouponGroup couponGroup, final List<Coupon> coupons) {
        return new CouponHistory(couponGroup, coupons);
    }

    public void validateIssueCoupon() {
        if (hasIssueHistory() && couponGroup.isNoRepeatable()) {
            throw new CouponIssueTypeException("쿠폰 발급에 실패했습니다. 반복 발급이 불가능한 쿠폰 그룹입니다.");
        }
        if (hasEnableCoupon() && couponGroup.isRepeatableAfterUsed()) {
            throw new CouponIssueTypeException("쿠폰 발급에 실패했습니다. 사용 후 발급이 가능한 쿠폰 그룹입니다.");
        }
    }

    private boolean hasIssueHistory() {
        return !coupons.isEmpty();
    }

    private boolean hasEnableCoupon() {
        return coupons.stream().anyMatch(Coupon::isEnabled);
    }
}
