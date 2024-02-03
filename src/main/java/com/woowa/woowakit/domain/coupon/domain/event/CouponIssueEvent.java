package com.woowa.woowakit.domain.coupon.domain.event;

import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CouponIssueEvent extends ApplicationEvent {

    private final CouponGroup couponGroup;

    public CouponIssueEvent(final Object source, final CouponGroup couponGroup) {
        super(source);
        this.couponGroup = couponGroup;
    }
}
