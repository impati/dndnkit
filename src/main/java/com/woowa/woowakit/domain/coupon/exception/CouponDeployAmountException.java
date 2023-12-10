package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class CouponDeployAmountException extends CouponException {

    public CouponDeployAmountException() {
        super("쿠폰 배포 수량은 0보다 크고 999,999,999보다 작아야 합니다.", HttpStatus.BAD_REQUEST);
    }
}
