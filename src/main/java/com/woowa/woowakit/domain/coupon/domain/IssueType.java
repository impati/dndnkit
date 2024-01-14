package com.woowa.woowakit.domain.coupon.domain;

import lombok.Getter;

@Getter
public enum IssueType {

    REPEATABLE("반복 발급"),
    REPEATABLE_AFTER_USED("사용 후 반복 발급"),
    NO_REPEATABLE("반복 발급 불가");

    private final String name;

    IssueType(String name) {
        this.name = name;
    }
}
