package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.CouponDeployAmountException;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;

@Getter
@Embeddable
public class CouponDeploy {

    private static final int NO_LIMIT_AMOUNT = 999_999_999;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_deploy_type")
    private CouponDeployType couponDeployType;

    @Column(name = "coupon_deploy_amount")
    private int deployAmount;

    protected CouponDeploy() {
    }

    private CouponDeploy(final CouponDeployType couponDeployType, final int deployAmount) {
        this.couponDeployType = couponDeployType;
        this.deployAmount = deployAmount;
    }

    public static CouponDeploy getDeployNoLimitInstance() {
        return new CouponDeploy(CouponDeployType.NO_LIMIT, NO_LIMIT_AMOUNT);
    }

    public static CouponDeploy getDeployLimitInstance(final int deployAmount) {
        if (deployAmount <= 0 || deployAmount >= NO_LIMIT_AMOUNT) {
            throw new CouponDeployAmountException();
        }
        return new CouponDeploy(CouponDeployType.LIMIT, deployAmount);
    }
}
