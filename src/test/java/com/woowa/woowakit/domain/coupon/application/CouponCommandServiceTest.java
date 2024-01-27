package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupStatus;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.domain.IssueType;
import com.woowa.woowakit.domain.coupon.exception.CouponGroupExpiredException;
import com.woowa.woowakit.domain.coupon.exception.CouponIssueTypeException;
import com.woowa.woowakit.domain.coupon.exception.IssueCouponException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    @DisplayName("쿠폰 그룹 배포 수량이 0 이라면 쿠폰 그룹 상태가 SHUTDOWN 이 된다.")
    void couponGroupShutDown() {
        int deployAmount = 1;
        CouponGroup persistentCouponGroup = couponGroupRepository.save(getDeployedCouponGroup(
                CouponDeploy.getDeployLimitInstance(deployAmount)
        ));
        couponDeployAmountRepository.deploy(persistentCouponGroup.getId(), deployAmount);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);

        couponCommandService.create(memberId, persistentCouponGroup.getId(), now);

        CouponGroup couponGroup = couponGroupRepository.findById(persistentCouponGroup.getId()).orElseThrow();
        assertThat(couponGroup.getCouponGroupStatus()).isEqualTo(CouponGroupStatus.SHUT_DOWN);
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
                .isInstanceOf(IssueCouponException.class)
                .hasMessage("쿠폰 그룹 상태가 배포 완료 상태여야합니다.");
        couponDeployAmountRepository.clear(persistentCouponGroup.getId());
    }

    @Test
    @DisplayName("쿠폰그룹으로부터 사용자 쿠폰을 동시에 발급하는데 동시성 문제가 발생하지 않아야한다.")
    void concurrency() throws InterruptedException {
        final int threadCount = 10;
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
        assertThat(couponDeployAmountRepository.getCouponDeployAmount(persistentCouponGroup.getId())).isEqualTo(0);
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
        LocalDate now = LocalDate.of(4024, 12, 31);

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
                getCouponGroup(CouponDeploy.getDeployNoLimitInstance(), IssueType.REPEATABLE)
        );
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);

        assertThatCode(() -> couponCommandService.create(memberId, persistentCouponGroup.getId(), now))
                .isInstanceOf(IssueCouponException.class);
    }

    @Test
    @DisplayName("회원이 이미 같은 쿠폰 그룹의 쿠폰을 가지고 있는 경우 '반복 발급 타입'일 경우에만 발급")
    void issueCouponByREPEATABLE() {
        CouponGroup couponGroup = couponGroupRepository.save(
                getDeployedCouponGroup(CouponDeploy.getDeployNoLimitInstance(), IssueType.REPEATABLE)
        );
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);
        couponCommandService.create(memberId, couponGroup.getId(), now);

        Long couponId = couponCommandService.create(memberId, couponGroup.getId(), now);

        assertThat(couponId).isNotNull();
    }

    @ParameterizedTest
    @DisplayName("회원이 이미 같은 쿠폰 그룹의 쿠폰을 가지고 있는 경우 '사용 후 재발급' , '재발급 불가'일 경우에 발급에 실패한다")
    @EnumSource(value = IssueType.class, mode = Mode.EXCLUDE, names = {"REPEATABLE"})
    void issueCouponFailByNOT_REPEATABLE(IssueType issueType) {
        CouponGroup couponGroup = couponGroupRepository.save(
                getDeployedCouponGroup(CouponDeploy.getDeployNoLimitInstance(), issueType)
        );
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);
        couponCommandService.create(memberId, couponGroup.getId(), now);

        assertThatCode(() -> couponCommandService.create(memberId, couponGroup.getId(), now))
                .isInstanceOf(CouponIssueTypeException.class)
                .hasMessageContaining("쿠폰 발급에 실패했습니다.");
    }

    @Transactional
    @ParameterizedTest
    @DisplayName("회원이 발급 하고자 하는 쿠폰 그룹의 쿠폰을 가지고 있지 않지만 이력이 있는 경우 '반복 발급 타입' , '사용 후 반복 발급' 경우에만 발급")
    @EnumSource(value = IssueType.class, mode = Mode.EXCLUDE, names = {"NO_REPEATABLE"})
    void issueCouponByExcludeNO_REPEATABLE(IssueType issueType) {
        CouponGroup couponGroup = couponGroupRepository.save(
                getDeployedCouponGroup(CouponDeploy.getDeployNoLimitInstance(), issueType)
        );
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);
        couponCommandService.create(memberId, couponGroup.getId(), now);
        List<Coupon> memberCoupons = couponRepository.findCoupon(memberId, now);
        memberCoupons.forEach(Coupon::used);

        Long couponId = couponCommandService.create(memberId, couponGroup.getId(), now);

        assertThat(couponId).isNotNull();
    }

    @Transactional
    @Test
    @DisplayName("회원이 발급 하고자 하는 쿠폰 그룹의 쿠폰을 가지고 있지 않지만 이력이 있는 경우 쿠폰 그룹 발급 타입이 '반복 불가'인 경우 발급에 실패한다.")
    void issueCouponFailByNO_REPEATABLE() {
        CouponGroup couponGroup = couponGroupRepository.save(
                getDeployedCouponGroup(CouponDeploy.getDeployNoLimitInstance(), IssueType.NO_REPEATABLE)
        );
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);
        couponCommandService.create(memberId, couponGroup.getId(), now);
        List<Coupon> memberCoupons = couponRepository.findCoupon(memberId, now);
        memberCoupons.forEach(Coupon::used);

        assertThatCode(() -> couponCommandService.create(memberId, couponGroup.getId(), now))
                .isInstanceOf(CouponIssueTypeException.class)
                .hasMessage("쿠폰 발급에 실패했습니다. 반복 발급이 불가능한 쿠폰 그룹입니다.");
    }

    @ParameterizedTest
    @EnumSource(value = IssueType.class)
    @DisplayName("회원이 쿠폰 그룹의 쿠폰을 가지고 있지도 , 이력도 없다면 모든 타입에 대해 발급한다.")
    void issueCouponGroupByAll(IssueType issueType) {
        CouponGroup couponGroup = couponGroupRepository.save(
                getDeployedCouponGroup(CouponDeploy.getDeployNoLimitInstance(), issueType)
        );
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);

        Long couponId = couponCommandService.create(memberId, couponGroup.getId(), now);

        assertThat(couponId).isNotNull();
    }

    private CouponGroup getCouponGroup(final CouponDeploy couponDeploy, final IssueType issueType) {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .couponDeploy(couponDeploy)
                .issueType(issueType)
                .build();
    }

    private CouponGroup getDeployedCouponGroup(final CouponDeploy couponDeploy) {
        CouponGroup couponGroup = getCouponGroup(couponDeploy, IssueType.REPEATABLE);
        couponGroup.deploy();

        return couponGroup;
    }

    private CouponGroup getDeployedCouponGroup(final CouponDeploy couponDeploy, final IssueType issueType) {
        CouponGroup couponGroup = getCouponGroup(couponDeploy, issueType);
        couponGroup.deploy();

        return couponGroup;
    }
}
