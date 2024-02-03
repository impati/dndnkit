package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class CouponIssueTypeException extends CouponException {

    public CouponIssueTypeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
