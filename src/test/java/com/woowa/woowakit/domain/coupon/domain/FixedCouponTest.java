package com.woowa.woowakit.domain.coupon.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.coupon.exception.DiscountAmountException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;

class FixedCouponTest {

	@Test
	@DisplayName("쿠폰 이름과 만료일 , 할인 금액과 최소 주문 금액, 쿠폰 적용 대상 입력 정액 할인 쿠폰을 생성할 수 있다.")
	void createFixedCoupon() {
		FixedCoupon fixedCoupon = FixedCoupon.builder()
			.discountAmount(1000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.memberId(1L)
			.name("한식 카테고리 밀키트 1000원 할인 쿠폰")
			.build();

		assertThat(fixedCoupon)
			.extracting(
				FixedCoupon::getDiscountAmount,
				FixedCoupon::getCouponTarget,
				FixedCoupon::getExpiryDate,
				FixedCoupon::getMinimumOrderAmount,
				FixedCoupon::getName)
			.contains(
				1000,
				CouponTarget.from(ProductCategory.KOREAN),
				LocalDate.of(2023, 12, 31),
				17000,
				"한식 카테고리 밀키트 1000원 할인 쿠폰");
	}

	@Test
	@DisplayName("정액 할인 쿠폰에서 최소 주문 금액보다 높은 할인액으로 쿠폰을 생성할 수 없다.")
	void createFixedCouponDiscountAmountFail() {
		int minimumAmount = 17000;
		int discountAmount = 18000;

		assertThatCode(() -> getFixedCoupon(minimumAmount, discountAmount))
			.isInstanceOf(DiscountAmountException.class)
			.hasMessage("정액 할인 금액은 0보다 커야하고 최소 주문 금액보다 낮아야합니다.");
	}

	@Test
	@DisplayName("정액 할인 쿠폰에서 쿠폰이름은 필수 입니다.")
	void createFixedCouponNameFail() {

		assertThatCode(this::getFixedCouponWithoutName)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 이름은 필수 입니다.");
	}

	@Test
	@DisplayName("정액 할인 쿠폰에서 쿠폰이름은 필수 입니다.")
	void createFixedCouponExpiryDateFail() {

		assertThatCode(this::getFixedCouponWithoutExpiryDate)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 만료일은 필수입니다.");
	}

	@Test
	@DisplayName("정액 할인 쿠폰에서 쿠폰이름은 필수 입니다.")
	void createFixedCouponCouponTargetFail() {

		assertThatCode(this::getFixedCouponWithoutCouponTarget)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 적용 대상 설정은 필수입니다.");
	}

	@Test
	@DisplayName("정액 할인 쿠폰에서 사용자 ID는 필수 입니다.")
	void createFixedCouponMemberFail() {

		assertThatCode(this::getFixedCouponWithoutMember)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("사용자 ID 는 필수입니다.");
	}

	private FixedCoupon getFixedCoupon(final int minimumAmount, final int discountAmount) {
		return getDefaultFixedCouponBuilder()
			.minimumOrderAmount(minimumAmount)
			.discountAmount(discountAmount)
			.build();
	}

	private FixedCoupon.FixedCouponBuilder getDefaultFixedCouponBuilder() {
		return FixedCoupon.builder()
			.discountAmount(1000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.memberId(1L)
			.name("한식 카테고리 밀키트 1000원 할인 쿠폰");
	}

	private FixedCoupon getFixedCouponWithoutExpiryDate() {
		return FixedCoupon.builder()
			.discountAmount(1000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.minimumOrderAmount(17000)
			.memberId(1L)
			.name("한식 카테고리 밀키트 1000원 할인 쿠폰")
			.build();
	}

	private FixedCoupon getFixedCouponWithoutCouponTarget() {
		return FixedCoupon.builder()
			.discountAmount(1000)
			.minimumOrderAmount(17000)
			.expiryDate(LocalDate.of(2023, 12, 31))
			.memberId(1L)
			.name("한식 카테고리 밀키트 1000원 할인 쿠폰")
			.build();
	}

	private FixedCoupon getFixedCouponWithoutName() {
		return FixedCoupon.builder()
			.discountAmount(1000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.memberId(1L)
			.build();
	}

	private FixedCoupon getFixedCouponWithoutMember() {
		return FixedCoupon.builder()
			.discountAmount(1000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.name("한식 카테고리 밀키트 1000원 할인 쿠폰")
			.build();
	}
}
