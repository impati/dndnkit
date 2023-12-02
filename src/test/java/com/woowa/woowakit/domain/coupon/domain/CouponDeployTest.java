package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.CouponDeployAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CouponDeployTest {

    @Test
    @DisplayName("무제한 쿠폰 배포 타입을 생성한다.")
    void getDeployNoLimitInstance() {
        CouponDeploy couponDeploy = CouponDeploy.getDeployNoLimitInstance();

        assertThat(couponDeploy.getCouponDeployType()).isEqualTo(CouponDeployType.NO_LIMIT);
    }

    @Test
    @DisplayName("제한 쿠폰 배포 타입을 생성하는 경우 쿠폰 배포 수량으로 생성한다.")
    void getDeployLimitInstance() {
        int deployAmount = 100;

        CouponDeploy couponDeploy = CouponDeploy.getDeployLimitInstance(deployAmount);

        assertThat(couponDeploy.getCouponDeployType()).isEqualTo(CouponDeployType.LIMIT);
        assertThat(couponDeploy.getDeployAmount()).isEqualTo(deployAmount);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 999_999_999, 1000_000_000})
    @DisplayName("제한 쿠폰 배포 타입을 생성하는 경우 쿠폰 배포 수량은 쿠폰 배포 수량은 0보다 크고 999,999,999보다 작아야 합니다.")
    void getDeployAmountFail(final int deployAmount) {
        assertThatCode(() -> CouponDeploy.getDeployLimitInstance(deployAmount))
                .isInstanceOf(CouponDeployAmountException.class);
    }
}