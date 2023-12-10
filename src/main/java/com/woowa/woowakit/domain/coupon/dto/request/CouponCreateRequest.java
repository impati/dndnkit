package com.woowa.woowakit.domain.coupon.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponCreateRequest {

    @NotNull(message = "쿠폰그룹 ID 는 필수입니다.")
    private Long couponGroupId;

    public static CouponCreateRequest from(final Long couponGroupId) {
        return new CouponCreateRequest(couponGroupId);
    }
}
