package com.woowa.woowakit.domain.coupon.application;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponFrame;
import com.woowa.woowakit.domain.coupon.domain.CouponFrameRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.exception.CouponFrameExpiredException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;

@SpringBootTest
class CouponCommandServiceTest {

	@Autowired
	private CouponFrameRepository couponFrameRepository;

	@Autowired
	private CouponCommandService couponCommandService;

	@Autowired
	private CouponQueryService couponQueryService;

	@Test
	@DisplayName("쿠폰틀로부터 사용자 쿠폰을 생성한다.")
	void createCoupon() {
		CouponFrame persistentCouponFrame = couponFrameRepository.save(getCouponFrame());
		Long memberId = 1L;
		LocalDate now = LocalDate.of(3023, 12, 31);

		Long couponId = couponCommandService.create(memberId, persistentCouponFrame.getId(), now);

		assertThat(couponQueryService.getCoupon(couponId))
			.extracting(
				Coupon::getName,
				Coupon::getMemberId,
				Coupon::getMinimumOrderAmount,
				Coupon::getCouponTarget,
				Coupon::getExpiryDate,
				Coupon::getDiscount)
			.contains(
				persistentCouponFrame.getName(),
				memberId,
				persistentCouponFrame.getMinimumOrderAmount(),
				persistentCouponFrame.getCouponTarget(),
				now.plusDays(persistentCouponFrame.getDuration().toDays()),
				persistentCouponFrame.getDiscount());
	}

	@Test
	@DisplayName("쿠폰틀이 만료되었다면 사용자 쿠폰을 생성하는데 실패한다.")
	void createCouponFail() {
		CouponFrame persistentCouponFrame = couponFrameRepository.save(getCouponFrame());
		Long memberId = 1L;
		LocalDate now = LocalDate.of(3024, 12, 31);

		assertThatCode(() -> couponCommandService.create(memberId, persistentCouponFrame.getId(), now))
			.isInstanceOf(CouponFrameExpiredException.class);
	}

	private CouponFrame getCouponFrame() {
		return CouponFrame.builder()
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
