package com.woowa.woowakit.domain.coupon.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.product.domain.ProductCategory;

class CouponFrameTest {

	@Test
	@DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성한다.")
	void createCouponFrame() {
		CouponFrame couponFrame = getDefaultCouponFrame();

		assertThat(couponFrame)
			.extracting(
				CouponFrame::getName,
				CouponFrame::getDuration,
				CouponFrame::getEndDate,
				CouponFrame::getMinimumOrderAmount,
				CouponFrame::getCouponTarget,
				CouponFrame::getCouponType,
				CouponFrame::getDiscount)
			.contains(
				"한식 밀키트 10% 할인 쿠폰",
				Duration.ofDays(3),
				LocalDate.of(2023, 12, 31),
				17000,
				CouponTarget.from(ProductCategory.KOREAN),
				CouponType.RATED,
				15);
	}

	@Test
	@DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 쿠폰 이름이 없다면 실패한다.")
	void createCouponFrameNameFail() {

		assertThatCode(this::getCouponFrameWithoutName)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 이름은 필수 입니다.");
	}

	@Test
	@DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 쿠폰 만료일이 없다면 실패한다.")
	void createCouponFrameEndDateFail() {

		assertThatCode(this::getCouponFrameWithoutEndDate)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 만료일은 필수 입니다.");
	}

	@Test
	@DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 쿠폰 적용 대상이 없다면 실패한다.")
	void createCouponFrameTargetFail() {

		assertThatCode(this::getCouponFrameWithoutCouponTarget)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 적용 대상은 필수 값입니다.");
	}

	@Test
	@DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 쿠폰 타입이 없다면 실패한다.")
	void createCouponFrameTypeFail() {

		assertThatCode(this::getCouponFrameWithoutType)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("쿠폰 타입은 필수 값입니다.");
	}

	@Test
	@DisplayName("쿠폰을 생성할 '쿠폰틀'을 생성할 때 할인 값은 양수여야한다. 실패한다.")
	void createCouponFrameDiscountFail() {

		assertThatCode(this::getCouponFrameWithoutDiscount)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("할인 금액 또는 할인률은 양수여야합니다.");
	}

	@Test
	@DisplayName("정률 쿠폰틀로 쿠폰을 생성한다.")
	void makeRateCoupon() {
		CouponFrame couponFrame = getCouponFrame(CouponType.RATED, 10);
		Long memberId = 1L;
		LocalDate now = LocalDate.of(3023, 12, 31);

		Coupon coupon = couponFrame.makeCoupon(memberId, now);

		assertThat(coupon)
			.extracting(Coupon::getDiscount, Coupon::getMemberId)
			.contains(10, memberId);
	}

	@Test
	@DisplayName("정액 쿠폰틀로 쿠폰을 생성한다.")
	void makeFixedCoupon() {
		CouponFrame couponFrame = getCouponFrame(CouponType.FIXED, 1000);
		Long memberId = 1L;
		LocalDate now = LocalDate.of(3023, 12, 31);

		Coupon coupon = couponFrame.makeCoupon(memberId, now);

		assertThat(coupon)
			.extracting(Coupon::getDiscount, Coupon::getMemberId)
			.contains(1000, memberId);
	}

	private CouponFrame getCouponFrameWithoutName() {
		return CouponFrame.builder()
			.duration(Duration.ofDays(3))
			.endDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.couponType(CouponType.RATED)
			.discount(15)
			.build();
	}

	private CouponFrame getCouponFrameWithoutCouponTarget() {
		return CouponFrame.builder()
			.name("한식 밀키트 10% 할인 쿠폰")
			.duration(Duration.ofDays(3))
			.endDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.couponType(CouponType.RATED)
			.discount(15)
			.build();
	}

	private CouponFrame getCouponFrameWithoutEndDate() {
		return CouponFrame.builder()
			.name("한식 밀키트 10% 할인 쿠폰")
			.duration(Duration.ofDays(3))
			.minimumOrderAmount(17000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.couponType(CouponType.RATED)
			.discount(15)
			.build();
	}

	private CouponFrame getCouponFrameWithoutType() {
		return CouponFrame.builder()
			.name("한식 밀키트 10% 할인 쿠폰")
			.duration(Duration.ofDays(3))
			.endDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.discount(15)
			.build();
	}

	private CouponFrame getCouponFrameWithoutDiscount() {
		return CouponFrame.builder()
			.name("한식 밀키트 10% 할인 쿠폰")
			.duration(Duration.ofDays(3))
			.endDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.couponType(CouponType.RATED)
			.build();
	}

	private CouponFrame getCouponFrame(final CouponType couponType, final int discount) {
		return getCouponFrameBuilder()
			.couponType(couponType)
			.discount(discount)
			.build();
	}

	private CouponFrame getDefaultCouponFrame() {
		return getCouponFrameBuilder()
			.build();
	}

	private CouponFrame.CouponFrameBuilder getCouponFrameBuilder() {
		return CouponFrame.builder()
			.name("한식 밀키트 10% 할인 쿠폰")
			.duration(Duration.ofDays(3))
			.endDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.couponType(CouponType.RATED)
			.discount(15);
	}
}
