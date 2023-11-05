package com.woowa.woowakit.domain.coupon.application;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.coupon.domain.CouponFrame;
import com.woowa.woowakit.domain.coupon.domain.CouponFrameRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.product.domain.ProductCategory;

@SpringBootTest
class CouponFrameQueryServiceTest {

	@Autowired
	private CouponFrameRepository couponFrameRepository;

	@Autowired
	private CouponFrameQueryService couponFrameQueryService;

	@AfterEach
	void tearDown() {
		couponFrameRepository.deleteAll();
	}

	@Test
	@DisplayName("유효한 쿠폰틀을 조회한다.")
	void getAvailableCouponFrames() {
		couponFrameRepository.save(getCouponFrameBuilder()
			.endDate(LocalDate.of(3023, 10, 16))
			.build());
		couponFrameRepository.save(getCouponFrameBuilder()
			.endDate(LocalDate.of(3023, 10, 17))
			.build());
		couponFrameRepository.save(getCouponFrameBuilder()
			.endDate(LocalDate.of(3023, 10, 18))
			.build());
		LocalDate now = LocalDate.of(3023, 10, 17);

		List<CouponFrame> couponFrames = couponFrameQueryService.getCouponFrames(now);

		assertThat(couponFrames).hasSize(2);
	}

	private CouponFrame.CouponFrameBuilder getCouponFrameBuilder() {
		return CouponFrame.builder()
			.name("한식 밀키트 10% 할인 쿠폰")
			.duration(Duration.ofDays(3))
			.endDate(LocalDate.of(3023, 12, 31))
			.minimumOrderAmount(17000)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.couponType(CouponType.RATED)
			.discount(15);
	}
}
