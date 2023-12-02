package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.CouponGroupExpiredException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CouponGroupTest {

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰그룹'을 생성한다.")
    void createCouponGroup() {
        CouponGroup couponGroup = getDefaultCouponGroup();

        assertThat(couponGroup)
                .extracting(
                        CouponGroup::getName,
                        CouponGroup::getDuration,
                        CouponGroup::getEndDate,
                        CouponGroup::getMinimumOrderAmount,
                        CouponGroup::getCouponTarget,
                        CouponGroup::getCouponType,
                        CouponGroup::getDiscount)
                .contains(
                        "한식 밀키트 10% 할인 쿠폰",
                        Duration.ofDays(3),
                        LocalDate.of(3023, 12, 31),
                        17000,
                        CouponTarget.from(ProductCategory.KOREAN),
                        CouponType.RATED,
                        15);
    }

    @Test
    @DisplayName("쿠폰틀 만료기간이 지나지 않으면 사용할 수 있다.")
    void isAvailableTrue() {
        CouponGroup couponGroup = getCouponGroupBuilder()
                .endDate(LocalDate.of(3023, 12, 31))
                .build();

        assertThat(couponGroup.isAvailable(LocalDate.of(3023, 12, 31))).isTrue();
        assertThat(couponGroup.isAvailable(LocalDate.of(3023, 12, 11))).isTrue();
    }

    @Test
    @DisplayName("쿠폰틀 만료기간이 지나면 사용할 수 없다.")
    void isAvailableFalse() {
        CouponGroup couponGroup = getCouponGroupBuilder()
                .endDate(LocalDate.of(3023, 12, 31))
                .build();

        assertThat(couponGroup.isAvailable(LocalDate.of(3024, 1, 1))).isFalse();
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 쿠폰 이름이 없다면 실패한다.")
    void createCouponGroupNameFail() {

        assertThatCode(this::getCouponGroupWithoutName)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 이름은 필수 입니다.");
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 쿠폰 만료일이 없다면 실패한다.")
    void createCouponGroupEndDateFail() {

        assertThatCode(this::getCouponGroupWithoutEndDate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 만료일은 필수 입니다.");
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 쿠폰 적용 대상이 없다면 실패한다.")
    void createCouponGroupTargetFail() {

        assertThatCode(this::getCouponGroupWithoutCouponTarget)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 적용 대상은 필수 값입니다.");
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 쿠폰 타입이 없다면 실패한다.")
    void createCouponGroupTypeFail() {

        assertThatCode(this::getCouponGroupWithoutType)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 타입은 필수 값입니다.");
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 할인 값은 양수여야한다. 실패한다.")
    void createCouponGroupDiscountFail() {

        assertThatCode(this::getCouponGroupWithoutDiscount)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인 금액 또는 할인률은 양수여야합니다.");
    }

    @Test
    @DisplayName("정률 쿠폰틀로 쿠폰을 생성한다.")
    void makeRateCoupon() {
        CouponGroup couponGroup = getCouponGroup(CouponType.RATED, 10);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);

        Coupon coupon = couponGroup.makeCoupon(memberId, now);

        assertThat(coupon)
                .extracting(Coupon::getDiscount, Coupon::getMemberId)
                .contains(10, memberId);
    }

    @Test
    @DisplayName("정액 쿠폰틀로 쿠폰을 생성한다.")
    void makeFixedCoupon() {
        CouponGroup couponGroup = getCouponGroup(CouponType.FIXED, 1000);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);

        Coupon coupon = couponGroup.makeCoupon(memberId, now);

        assertThat(coupon)
                .extracting(Coupon::getDiscount, Coupon::getMemberId)
                .contains(1000, memberId);
    }

    @Test
    @DisplayName("쿠폰틀이 만료되면 쿠폰을 생성하는데 실패한다.")
    void makeFixedCouponFail() {
        CouponGroup couponGroup = getCouponGroup(CouponType.FIXED, 1000);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3033, 12, 31);

        assertThatCode(() -> couponGroup.makeCoupon(memberId, now))
                .isInstanceOf(CouponGroupExpiredException.class)
                .hasMessage("쿠폰틀이 만료되었습니다.");
    }

    private CouponGroup getCouponGroupWithoutName() {
        return CouponGroup.builder()
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .build();
    }

    private CouponGroup getCouponGroupWithoutCouponTarget() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .couponType(CouponType.RATED)
                .discount(15)
                .build();
    }

    private CouponGroup getCouponGroupWithoutEndDate() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .build();
    }

    private CouponGroup getCouponGroupWithoutType() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .discount(15)
                .build();
    }

    private CouponGroup getCouponGroupWithoutDiscount() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .build();
    }

    private CouponGroup getCouponGroup(final CouponType couponType, final int discount) {
        return getCouponGroupBuilder()
                .couponType(couponType)
                .discount(discount)
                .build();
    }

    private CouponGroup getDefaultCouponGroup() {
        return getCouponGroupBuilder()
                .build();
    }

    private CouponGroup.CouponGroupBuilder getCouponGroupBuilder() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15);
    }
}
