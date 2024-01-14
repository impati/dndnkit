package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.domain.IssueType;
import com.woowa.woowakit.domain.coupon.exception.CouponGroupExpiredException;
import com.woowa.woowakit.domain.coupon.exception.ExhaustedCouponDeployAmountException;
import com.woowa.woowakit.domain.coupon.exception.IssueCouponException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class CouponCommandServiceTest {

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponCommandService couponCommandService;

    @Autowired
    private CouponQueryService couponQueryService;

    @Autowired
    private CouponDeployAmountRepository couponDeployAmountRepository;

    @AfterEach
    void tearDown() {
        couponRepository.deleteAll();
        couponGroupRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰그룹으로부터 사용자 쿠폰을 발급한다.")
    void issueCoupon() {
        CouponGroup persistentCouponGroup = couponGroupRepository.save(getDeployedCouponGroup(
                CouponDeploy.getDeployNoLimitInstance()
        ));
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);

        Long couponId = couponCommandService.create(memberId, persistentCouponGroup.getId(), now);

        assertThat(couponQueryService.getCoupon(couponId))
                .extracting(
                        Coupon::getName,
                        Coupon::getMemberId,
                        Coupon::getMinimumOrderAmount,
                        Coupon::getCouponTarget,
                        Coupon::getExpiryDate,
                        Coupon::getDiscount)
                .contains(
                        persistentCouponGroup.getName(),
                        memberId,
                        persistentCouponGroup.getMinimumOrderAmount(),
                        persistentCouponGroup.getCouponTarget(),
                        now.plusDays(persistentCouponGroup.getDuration().toDays()),
                        persistentCouponGroup.getDiscount());
    }

    @Test
    @DisplayName("쿠폰그룹으로부터 사용자 쿠폰을 발급하면 쿠폰그룹 배포수량을 감소한다.")
    void whenIssueThenCouponDeployAmount() {
        int deployAmount = 10;
        CouponGroup persistentCouponGroup = couponGroupRepository.save(getDeployedCouponGroup(
                CouponDeploy.getDeployLimitInstance(deployAmount)
        ));
        couponDeployAmountRepository.deploy(persistentCouponGroup.getId(), deployAmount);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);

        couponCommandService.create(memberId, persistentCouponGroup.getId(), now);

        int afterDeployAmount = couponDeployAmountRepository.getCouponDeployAmount(persistentCouponGroup.getId());
        assertThat(afterDeployAmount).isEqualTo(deployAmount - 1);
        couponDeployAmountRepository.clear(persistentCouponGroup.getId());
    }

    @Test
    @DisplayName("쿠폰그룹으로부터 사용자 쿠폰을 발급하는데 배포 수량이 없다면 실패한다.")
    void whenIssueThenThrow() {
        int deployAmount = 1;
        CouponGroup persistentCouponGroup = couponGroupRepository.save(getDeployedCouponGroup(
                CouponDeploy.getDeployLimitInstance(deployAmount)
        ));
        couponDeployAmountRepository.deploy(persistentCouponGroup.getId(), deployAmount);
        Long memberId = 1L;
        Long otherMemberId = 2L;
        LocalDate now = LocalDate.of(3023, 12, 31);
        couponCommandService.create(otherMemberId, persistentCouponGroup.getId(), now);

        assertThatCode(() -> couponCommandService.create(memberId, persistentCouponGroup.getId(), now))
                .isInstanceOf(ExhaustedCouponDeployAmountException.class);
        couponDeployAmountRepository.clear(persistentCouponGroup.getId());
    }

    @ParameterizedTest
    @DisplayName("쿠폰그룹으로부터 사용자 쿠폰을 동시에 발급하는데 동시성 문제가 발생하지 않아야한다.")
    @ValueSource(ints = {10, 100})
    void concurrency(final int threadCount) throws InterruptedException {
        int deployAmount = 5;
        CouponGroup persistentCouponGroup = couponGroupRepository.save(getDeployedCouponGroup(
                CouponDeploy.getDeployLimitInstance(deployAmount)
        ));
        couponDeployAmountRepository.deploy(persistentCouponGroup.getId(), deployAmount);
        LocalDate now = LocalDate.of(3023, 12, 31);

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int id = i + 1;
            executorService.submit(() -> {
                try {
                    Long memberId = (long) id;
                    couponCommandService.create(memberId, persistentCouponGroup.getId(), now);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        assertThat(couponRepository.count()).isEqualTo(deployAmount);
        couponDeployAmountRepository.clear(persistentCouponGroup.getId());
    }

    @Test
    @DisplayName("쿠폰그룹이 만료되었다면 사용자 쿠폰을 생성하는데 실패한다.")
    void createCouponFail() {
        int deployAmount = 100;
        CouponGroup persistentCouponGroup = couponGroupRepository.save(getDeployedCouponGroup(
                CouponDeploy.getDeployLimitInstance(deployAmount)
        ));
        couponDeployAmountRepository.deploy(persistentCouponGroup.getId(), deployAmount);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3024, 12, 31);

        assertThatCode(() -> couponCommandService.create(memberId, persistentCouponGroup.getId(), now))
                .isInstanceOf(CouponGroupExpiredException.class);
        assertThat(couponDeployAmountRepository.getCouponDeployAmount(persistentCouponGroup.getId()))
                .isEqualTo(deployAmount);
        couponDeployAmountRepository.clear(persistentCouponGroup.getId());
    }

    @Test
    @DisplayName("쿠폰 그룹이 배포상태가 아니라면 사용자 쿠폰을 생성하는데 실패한다.")
    void issueCouponFail() {
        CouponGroup persistentCouponGroup = couponGroupRepository.save(
                getCouponGroup(CouponDeploy.getDeployNoLimitInstance())
        );
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);

        assertThatCode(() -> couponCommandService.create(memberId, persistentCouponGroup.getId(), now))
                .isInstanceOf(IssueCouponException.class);
    }

    private CouponGroup getCouponGroup(final CouponDeploy couponDeploy) {
        return CouponGroup.builder()
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

    private CouponGroup getDeployedCouponGroup(final CouponDeploy couponDeploy) {
        CouponGroup couponGroup = getCouponGroup(couponDeploy);
        couponGroup.deploy();

        return couponGroup;
    }
}
