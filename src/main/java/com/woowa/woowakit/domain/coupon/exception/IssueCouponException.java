package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class IssueCouponException extends CouponException {

    public IssueCouponException() {
        super("쿠폰 그룹 상태가 배포 완료 상태여야합니다.", HttpStatus.BAD_REQUEST);
    }
}
