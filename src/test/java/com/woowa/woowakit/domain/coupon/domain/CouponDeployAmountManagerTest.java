package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponDeployAmountManagerTest {

    private CouponDeployAmountMemoryRepository couponDeployAmountMemoryRepository;
    private CouponDeployAmountManager couponDeployAmountManager;

    @BeforeEach
    void setUp() {
        couponDeployAmountMemoryRepository = new CouponDeployAmountMemoryRepository();
        couponDeployAmountManager = new CouponDeployAmountManager(couponDeployAmountMemoryRepository);
    }

    @Test
    @DisplayName("쿠폰 그룹이 제한 타입이라면 배포 수량 저장소에 수량을 감소한다.")
    void decreaseDeployAmount() {
        int deployAmount = 100;
        CouponDeploy couponDeploy = CouponDeploy.getDeployLimitInstance(deployAmount);
        CouponGroup couponGroup = getCouponGroup(couponDeploy);
        couponGroup.deploy();
        couponDeployAmountMemoryRepository.deploy(couponGroup.getId(), couponDeploy.getDeployAmount());

        couponDeployAmountManager.decreaseDeployAmount(couponGroup);

        assertThat(couponDeployAmountMemoryRepository.getCouponDeployAmount(couponGroup.getId()))
                .isEqualTo(deployAmount - 1);
    }

    @Test
    @DisplayName("배포 수량 저장소에 수량을 감소하고 난 뒤 수량이 0이라면 쿠폰 그룹의 상태는 SHUTDOWN 이 된다.")
    void decreaseDeployAmountShutDown() {
        int deployAmount = 1;
        CouponDeploy couponDeploy = CouponDeploy.getDeployLimitInstance(deployAmount);
        CouponGroup couponGroup = getCouponGroup(couponDeploy);
        couponGroup.deploy();
        couponDeployAmountMemoryRepository.deploy(couponGroup.getId(), couponDeploy.getDeployAmount());

        couponDeployAmountManager.decreaseDeployAmount(couponGroup);

        assertThat(couponDeployAmountMemoryRepository.getCouponDeployAmount(couponGroup.getId())).isZero();
        assertThat(couponGroup.getCouponGroupStatus()).isEqualTo(CouponGroupStatus.SHUT_DOWN);
    }

    @Test
    @DisplayName("쿠폰 그룹 배포 수량을 증가하면 저장소에 배포 수량이 증가한다.")
    void increase() {
        int deployAmount = 1;
        CouponDeploy couponDeploy = CouponDeploy.getDeployLimitInstance(deployAmount);
        CouponGroup couponGroup = getCouponGroup(couponDeploy);
        couponGroup.deploy();
        couponDeployAmountMemoryRepository.deploy(couponGroup.getId(), couponDeploy.getDeployAmount());

        couponDeployAmountManager.increase(couponGroup);

        assertThat(couponDeployAmountMemoryRepository.getCouponDeployAmount(couponGroup.getId()))
                .isEqualTo(deployAmount + 1);
        assertThat(couponGroup.getCouponGroupStatus()).isEqualTo(CouponGroupStatus.DEPLOY);
    }

    private CouponGroup getCouponGroup(final CouponDeploy couponDeploy) {
        return CouponGroup.builder()
                .id(1L)
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .couponDeploy(couponDeploy)
                .issueType(IssueType.REPEATABLE)
                .build();
    }
}
