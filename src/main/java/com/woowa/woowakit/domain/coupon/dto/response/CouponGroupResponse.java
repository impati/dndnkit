package com.woowa.woowakit.domain.coupon.dto.response;

import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponGroupResponse {

    private String name;
    private Long durationDay;
    private LocalDate endDate;
    private CouponType couponType;
    private int minimumOrderAmount;
    private int discount;
    private CouponTarget couponTarget;

    public static CouponGroupResponse from(final CouponGroup couponGroup) {
        return new CouponGroupResponse(
                couponGroup.getName(),
                couponGroup.getDuration().toDays(),
                couponGroup.getEndDate(),
                couponGroup.getCouponType(),
                couponGroup.getMinimumOrderAmount(),
                couponGroup.getDiscount(),
                couponGroup.getCouponTarget()
        );
    }
}
