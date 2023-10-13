package com.woowa.woowakit.domain.coupon.application;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.coupon.domain.CouponFrame;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.dto.request.ProductCouponFrameCreateRequest;

@SpringBootTest
class CouponFrameServiceTest {

	@Autowired
	private CouponFrameCommandService couponFrameCommandService;

	@Autowired
	private CouponFrameQueryService couponFrameQueryService;

	@Test
	@DisplayName("쿠폰 적용 대상과 쿠폰 정보로 '쿠폰틀' 을 생성하고 ID 를 반환한다.")
	void createCouponFrame() {
		Long productId = 1L;
		CouponTarget couponTarget = CouponTarget.from(productId);
		ProductCouponFrameCreateRequest request = ProductCouponFrameCreateRequest.of(
			"default",
			1L,
			LocalDate.of(3023, 12, 31),
			CouponType.FIXED,
			productId,
			17000,
			15000
		);

		Long couponFrameId = couponFrameCommandService.create(couponTarget, request);

		assertThat(couponFrameQueryService.getCouponFrame(couponFrameId))
			.extracting(
				CouponFrame::getName,
				CouponFrame::getDuration,
				CouponFrame::getEndDate,
				CouponFrame::getMinimumOrderAmount,
				CouponFrame::getCouponTarget,
				CouponFrame::getCouponType,
				CouponFrame::getDiscount)
			.contains(
				"default",
				Duration.ofDays(1),
				LocalDate.of(3023, 12, 31),
				17000,
				CouponTarget.from(productId),
				CouponType.FIXED,
				15000);
	}
}
