package com.woowa.woowakit.domain.coupon.dto.response;

import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CouponGroupResponses {

    private List<CouponGroupResponse> couponGroupResponses;

    public static CouponGroupResponses from(final List<CouponGroup> couponGroups) {
        return new CouponGroupResponses(couponGroups.stream()
                .map(CouponGroupResponse::from)
                .collect(Collectors.toList()));
    }
}
