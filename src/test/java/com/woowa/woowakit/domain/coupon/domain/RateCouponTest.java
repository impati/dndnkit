package com.woowa.woowakit.domain.coupon.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.coupon.exception.DiscountRateException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;

class RateCouponTest {

	@Test
	@DisplayName("쿠폰 이름과 만료일 , 할인률 과 최소 주문 금액, 쿠폰 적용 대상 입력 정률 할인 쿠폰을 생성할 수 있다.")
	void createRateCoupon() {
		RateCoupon rateCoupon = RateCoupon.builder()
			.memberId(1L)
			.discountRate(10)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.name("한식 카테고리 밀키트 10% 할인 쿠폰")
			.build();

		assertThat(rateCoupon)
			.extracting(
				RateCoupon::getDiscountRate,
				RateCoupon::getCouponTarget,
				RateCoupon::getExpiryDate,
				RateCoupon::getMinimumOrderAmount,
				RateCoupon::getName)
			.contains(
				10,
				CouponTarget.from(ProductCategory.KOREAN),
				LocalDate.of(2023, 12, 31),
				17000,
				"한식 카테고리 밀키트 10% 할인 쿠폰");
	}

	@Test
	@DisplayName("정률 할인 쿠폰에서 쿠폰이름은 필수 입니다.")
	void createFixedCouponNameFail() {

		assertThatCode(this::getRateCouponWithoutName)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 이름은 필수 입니다.");
	}

	@Test
	@DisplayName("정률 할인 쿠폰에서 쿠폰이름은 필수 입니다.")
	void createFixedCouponExpiryDateFail() {

		assertThatCode(this::getRateCouponWithoutExpiryDate)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 만료일은 필수입니다.");
	}

	@Test
	@DisplayName("정률 할인 쿠폰에서 쿠폰이름은 필수 입니다.")
	void createFixedCouponCouponTargetFail() {

		assertThatCode(this::getRateCouponWithoutCouponTarget)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 적용 대상 설정은 필수입니다.");
	}

	@Test
	@DisplayName("100퍼센트를 넘긴 정률 할인 쿠폰은 생성할 수 없다.")
	void createRateCouponRateFail() {
		assertThatCode(() -> getRateCoupon(101, "한식 카테고리 밀키트 101% 할인 쿠폰"))
			.isInstanceOf(DiscountRateException.class)
			.hasMessage("정률 할인은 정수이며 0보다 크고 100보다 작거나 같아야합니다.");
	}

	@Test
	@DisplayName("사용자 ID 가 없는 정률 할인 쿠폰은 생성할 수 없다.")
	void createRateMemberIdFail() {
		assertThatCode(this::getRateCouponWithoutMember)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("사용자 ID 는 필수입니다.");
	}

	private RateCoupon getRateCoupon(final int discountRate, final String name) {
		return getRateCouponBuilder()
			.discountRate(discountRate)
			.name(name)
			.build();
	}

	private RateCoupon getRateCouponWithoutName() {
		return RateCoupon.builder()
			.discountRate(10)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.memberId(1L)
			.build();
	}

	private RateCoupon getRateCouponWithoutMember() {
		return RateCoupon.builder()
			.discountRate(10)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.name("한식 카테고리 밀키트 10% 할인 쿠폰")
			.build();
	}

	private RateCoupon getRateCouponWithoutExpiryDate() {
		return RateCoupon.builder()
			.discountRate(10)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.minimumOrderAmount(17000)
			.memberId(1L)
			.name("한식 카테고리 밀키트 10% 할인 쿠폰")
			.build();
	}

	private RateCoupon getRateCouponWithoutCouponTarget() {
		return RateCoupon.builder()
			.discountRate(10)
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.memberId(1L)
			.name("한식 카테고리 밀키트 10% 할인 쿠폰")
			.build();
	}

	private RateCoupon.RateCouponBuilder getRateCouponBuilder() {
		return RateCoupon.builder()
			.discountRate(10)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.memberId(1L)
			.name("한식 카테고리 밀키트 10% 할인 쿠폰");
	}
}
