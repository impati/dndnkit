package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.DiscountAmountException;
import com.woowa.woowakit.domain.coupon.exception.DiscountRateException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CouponCreateTest {

    @Test
    @DisplayName("쿠폰 이름과 만료일 , 할인 금액과 최소 주문 금액, 쿠폰 적용 대상 입력 정액 할인 쿠폰을 생성할 수 있다.")
    void createFixedCoupon() {
        Coupon fixedCoupon = Coupon.builder()
                .discount(1000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .expiryDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .memberId(1L)
                .name("한식 카테고리 밀키트 1000원 할인 쿠폰")
                .couponType(CouponType.FIXED)
                .build();

        assertThat(fixedCoupon)
                .extracting(
                        Coupon::getDiscount,
                        Coupon::getCouponTarget,
                        Coupon::getExpiryDate,
                        Coupon::getMinimumOrderAmount,
                        Coupon::getName,
                        Coupon::isEnabled)
                .contains(
                        1000,
                        CouponTarget.from(ProductCategory.KOREAN),
                        LocalDate.of(3023, 12, 31),
                        17000,
                        "한식 카테고리 밀키트 1000원 할인 쿠폰",
                        true);
    }

    @Test
    @DisplayName("쿠폰 이름과 만료일 , 할인 금액과 최소 주문 금액, 쿠폰 적용 대상 입력 정률 할인 쿠폰을 생성할 수 있다.")
    void createRateCoupon() {
        Coupon rateCoupon = Coupon.builder()
                .discount(10)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .expiryDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .memberId(1L)
                .name("한식 카테고리 밀키트 1000원 할인 쿠폰")
                .couponType(CouponType.RATED)
                .build();

        assertThat(rateCoupon)
                .extracting(
                        Coupon::getDiscount,
                        Coupon::getCouponTarget,
                        Coupon::getExpiryDate,
                        Coupon::getMinimumOrderAmount,
                        Coupon::getName,
                        Coupon::isEnabled)
                .contains(
                        10,
                        CouponTarget.from(ProductCategory.KOREAN),
                        LocalDate.of(3023, 12, 31),
                        17000,
                        "한식 카테고리 밀키트 1000원 할인 쿠폰",
                        true);
    }

    @Test
    @DisplayName("정액 할인 쿠폰에서 최소 주문 금액보다 높은 할인액으로 쿠폰을 생성할 수 없다.")
    void createFixedCouponDiscountFail() {
        int minimumAmount = 17000;
        int discountAmount = 18000;

        assertThatCode(() -> getFixedCoupon(minimumAmount, discountAmount))
                .isInstanceOf(DiscountAmountException.class)
                .hasMessage("정액 할인 금액은 0보다 커야하고 최소 주문 금액보다 낮아야합니다.");
    }

    @Test
    @DisplayName("정률 할인 쿠폰에서 할인률은 0보다 작거나 같거나 100 보다 크다면 쿠폰을 생성할 수 없다.")
    void createRateCouponDiscountFail() {
        int discountAmount = 101;

        assertThatCode(() -> getRateCoupon(discountAmount))
                .isInstanceOf(DiscountRateException.class)
                .hasMessage("정률 할인은 정수이며 0보다 크고 100보다 작거나 같아야합니다.");
    }

    @Test
    @DisplayName("할인 쿠폰에서 쿠폰타입은 필수 입니다.")
    void createFixedCouponTypeFail() {

        assertThatCode(this::getFixedCouponWithoutCouponType)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 타입 설정은 필수입니다.");
    }

    @Test
    @DisplayName("할인 쿠폰에서 쿠폰이름은 필수 입니다.")
    void createFixedCouponNameFail() {

        assertThatCode(this::getFixedCouponWithoutName)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 이름은 필수 입니다.");
    }

    @Test
    @DisplayName("할인 쿠폰에서 쿠폰이름은 필수 입니다.")
    void createFixedCouponExpiryDateFail() {

        assertThatCode(this::getFixedCouponWithoutExpiryDate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 만료일은 필수입니다.");
    }

    @Test
    @DisplayName("할인 쿠폰에서 쿠폰이름은 필수 입니다.")
    void createFixedCouponCouponTargetFail() {

        assertThatCode(this::getFixedCouponWithoutCouponTarget)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 적용 대상 설정은 필수입니다.");
    }

    @Test
    @DisplayName("할인 쿠폰에서 사용자 ID는 필수 입니다.")
    void createFixedCouponMemberFail() {

        assertThatCode(this::getFixedCouponWithoutMember)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자 ID 는 필수입니다.");
    }

    private Coupon getFixedCoupon(final int minimumAmount, final int discountAmount) {
        return getDefaultFixedCouponBuilder()
                .minimumOrderAmount(minimumAmount)
                .discount(discountAmount)
                .build();
    }

    private Coupon getRateCoupon(final int discountRate) {
        return getDefaultFixedCouponBuilder()
                .couponType(CouponType.RATED)
                .discount(discountRate)
                .build();
    }

    private Coupon.CouponBuilder getDefaultFixedCouponBuilder() {
        return Coupon.builder()
                .discount(1000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .expiryDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .memberId(1L)
                .couponType(CouponType.FIXED)
                .name("한식 카테고리 밀키트 1000원 할인 쿠폰");
    }

    private Coupon getFixedCouponWithoutExpiryDate() {
        return Coupon.builder()
                .discount(1000)
                .couponType(CouponType.FIXED)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .minimumOrderAmount(17000)
                .memberId(1L)
                .name("한식 카테고리 밀키트 1000원 할인 쿠폰")
                .build();
    }

    private Coupon getFixedCouponWithoutCouponTarget() {
        return Coupon.builder()
                .discount(1000)
                .couponType(CouponType.FIXED)
                .minimumOrderAmount(17000)
                .expiryDate(LocalDate.of(3023, 12, 31))
                .memberId(1L)
                .name("한식 카테고리 밀키트 1000원 할인 쿠폰")
                .build();
    }

    private Coupon getFixedCouponWithoutName() {
        return Coupon.builder()
                .discount(1000)
                .couponType(CouponType.FIXED)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .expiryDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .memberId(1L)
                .build();
    }

    private Coupon getFixedCouponWithoutCouponType() {
        return Coupon.builder()
                .discount(1000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .expiryDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .memberId(1L)
                .name("한식 카테고리 밀키트 1000원 할인 쿠폰")
                .build();
    }

    private Coupon getFixedCouponWithoutMember() {
        return Coupon.builder()
                .discount(1000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.FIXED)
                .expiryDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .name("한식 카테고리 밀키트 1000원 할인 쿠폰")
                .build();
    }
}
