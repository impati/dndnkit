package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.domain.IssueType;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class CouponQueryServiceTest {

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponQueryService couponQueryService;

    @AfterEach
    void tearDown() {
        couponRepository.deleteAll();
        couponGroupRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 쿠폰들을 조회해온다.")
    void getCouponsByMember() {
        CouponGroup persistentAllCouponGroup = couponGroupRepository.save(getDeployedCouponGroup(CouponTarget.all()));
        CouponGroup persistentBrandCouponGroup = couponGroupRepository.save(
                getDeployedCouponGroup(CouponTarget.from(ProductBrand.MOKRAN))
        );
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);
        couponRepository.save(persistentAllCouponGroup.issueCoupon(memberId, now));
        couponRepository.save(persistentBrandCouponGroup.issueCoupon(memberId, now));

        List<Coupon> coupons = couponQueryService.getCouponsByMember(memberId, now);

        assertThat(coupons).hasSize(2)
                .extracting(
                        Coupon::getName,
                        Coupon::getCouponTarget,
                        Coupon::getMemberId)
                .contains(
                        tuple("한식 밀키트 10% 할인 쿠폰", CouponTarget.all(), memberId),
                        tuple("한식 밀키트 10% 할인 쿠폰", CouponTarget.from(ProductBrand.MOKRAN), memberId));
    }

    @Test
    @DisplayName("사용자 쿠폰들을 조회하는데 만료된 쿠폰은 조회하지 않는다.")
    void getNotExpiredCouponsByMember() {
        CouponGroup persistentAllCouponGroup = couponGroupRepository.save(getDeployedCouponGroup(CouponTarget.all()));
        CouponGroup persistentBrandCouponGroup = couponGroupRepository.save(
                getDeployedCouponGroup(CouponTarget.from(ProductBrand.MOKRAN))
        );
        Long memberId = 1L;
        couponRepository.save(persistentAllCouponGroup.issueCoupon(memberId, LocalDate.of(3023, 12, 31)));
        couponRepository.save(persistentBrandCouponGroup.issueCoupon(memberId, LocalDate.of(3023, 12, 11)));

        List<Coupon> coupons = couponQueryService.getCouponsByMember(memberId, LocalDate.of(3023, 12, 30));

        assertThat(coupons).hasSize(1)
                .extracting(
                        Coupon::getName,
                        Coupon::getCouponTarget,
                        Coupon::getMemberId)
                .contains(
                        tuple("한식 밀키트 10% 할인 쿠폰", CouponTarget.all(), memberId));
    }

    private CouponGroup getDeployedCouponGroup(final CouponTarget couponTarget) {
        CouponGroup couponGroup = getCouponGroup(couponTarget);
        couponGroup.deploy();

        return couponGroup;
    }

    private CouponGroup getCouponGroup(final CouponTarget couponTarget) {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(couponTarget)
                .couponType(CouponType.RATED)
                .discount(15)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance())
                .issueType(IssueType.REPEATABLE)
                .build();
    }
}
