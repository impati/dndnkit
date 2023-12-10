package com.woowa.woowakit.domain.fixture;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.Coupon.CouponBuilder;
import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup.CouponGroupBuilder;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.Duration;
import java.time.LocalDate;

public class CouponFixture {

    public static CouponGroupBuilder getDefaultCouponGroupBuilder() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance())
                .discount(15);
    }

    public static CouponBuilder getAllCouponBuilder() {
        return Coupon.builder()
                .discount(1000)
                .couponType(CouponType.FIXED)
                .couponTarget(CouponTarget.all())
                .expiryDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .memberId(1L)
                .name("한식 카테고리 밀키트 1000원 할인 쿠폰");
    }
}
