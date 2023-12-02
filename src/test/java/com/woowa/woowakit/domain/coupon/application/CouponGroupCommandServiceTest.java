package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.exception.NotFoundCouponDeployAmountException;
import com.woowa.woowakit.domain.fixture.CouponFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class CouponGroupCommandServiceTest {

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponGroupCommandService couponGroupCommandService;

    @Autowired
    private CouponDeployAmountRepository couponDeployAmountRepository;

    @AfterEach
    void tearDown() {
        couponGroupRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰 그룹 배포 시 배포 수량 만큼 수량 저장소에 저장한다.")
    void deployCouponGroup() {
        int deployAmount = 1000;
        CouponGroup couponGroup = couponGroupRepository.save(CouponFixture.getDefaultCouponGroupBuilder()
                .couponDeploy(CouponDeploy.getDeployLimitInstance(deployAmount))
                .build());

        couponGroupCommandService.deploy(couponGroup.getId());

        int couponDeployAmount = couponDeployAmountRepository.getCouponDeployAmount(couponGroup.getId());
        assertThat(couponDeployAmount).isEqualTo(deployAmount);
        clearResource(couponGroup);
    }

    @Test
    @DisplayName("쿠폰 그룹 배포 시 무제한 수량인 경우 수량 저장소에 저장하지 않는다.")
    void deployCouponGroupNoLimit() {
        CouponGroup couponGroup = couponGroupRepository.save(CouponFixture.getDefaultCouponGroupBuilder()
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance())
                .build());

        couponGroupCommandService.deploy(couponGroup.getId());

        assertThatCode(() -> couponDeployAmountRepository.getCouponDeployAmount(couponGroup.getId()))
                .isInstanceOf(NotFoundCouponDeployAmountException.class);
    }

    private void clearResource(final CouponGroup couponGroup) {
        couponDeployAmountRepository.clear(couponGroup.getId());
    }
}