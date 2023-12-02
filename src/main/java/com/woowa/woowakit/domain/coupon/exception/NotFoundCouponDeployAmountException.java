package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class NotFoundCouponDeployAmountException extends CouponException {

    public NotFoundCouponDeployAmountException() {
        super("쿠폰 그룹 ID 에 대한 배포 수량 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
