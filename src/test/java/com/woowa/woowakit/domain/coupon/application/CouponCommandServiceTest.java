package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.exception.CouponGroupExpiredException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @AfterEach
    void tearDown() {
        couponRepository.deleteAll();
        couponGroupRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰틀로부터 사용자 쿠폰을 생성한다.")
    void createCoupon() {
        CouponGroup persistentCouponGroup = couponGroupRepository.save(getCouponGroup());
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
    @DisplayName("쿠폰틀이 만료되었다면 사용자 쿠폰을 생성하는데 실패한다.")
    void createCouponFail() {
        CouponGroup persistentCouponGroup = couponGroupRepository.save(getCouponGroup());
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3024, 12, 31);

        assertThatCode(() -> couponCommandService.create(memberId, persistentCouponGroup.getId(), now))
                .isInstanceOf(CouponGroupExpiredException.class);
    }

    private CouponGroup getCouponGroup() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .build();
    }
}
