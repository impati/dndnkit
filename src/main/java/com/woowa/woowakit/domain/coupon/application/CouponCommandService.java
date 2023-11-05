package com.woowa.woowakit.domain.coupon.application;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponFrame;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponCommandService {

	private final CouponRepository couponRepository;
	private final CouponFrameQueryService couponFrameQueryService;

	public Long create(
		final Long memberId,
		final Long couponFrameId,
		final LocalDate now
	) {
		CouponFrame couponFrame = couponFrameQueryService.getCouponFrame(couponFrameId);

		Coupon coupon = couponFrame.makeCoupon(memberId, now);

		return couponRepository.save(coupon).getId();
	}
}
