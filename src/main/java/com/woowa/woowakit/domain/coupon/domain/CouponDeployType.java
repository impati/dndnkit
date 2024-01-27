package com.woowa.woowakit.domain.coupon.domain;

import lombok.Getter;

@Getter
public enum CouponDeployType {

    NO_LIMIT("무제한 수량 발급"),
    LIMIT("제한 수량 발급");

    private final String name;

    CouponDeployType(String name) {
        this.name = name;
    }
}
