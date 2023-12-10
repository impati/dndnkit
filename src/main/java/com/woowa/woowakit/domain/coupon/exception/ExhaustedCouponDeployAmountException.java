package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class ExhaustedCouponDeployAmountException extends CouponException {

    public ExhaustedCouponDeployAmountException() {
        super("쿠폰 배포 수량이 모두 소진되었습니다.", HttpStatus.BAD_REQUEST);
    }
}
