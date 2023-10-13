package com.woowa.woowakit.domain.coupon.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.coupon.domain.CouponFrame;
import com.woowa.woowakit.domain.coupon.domain.CouponFrameRepository;
import com.woowa.woowakit.domain.coupon.exception.NotFoundCouponFrameException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponFrameQueryService {

	private final CouponFrameRepository couponFrameRepository;

	public CouponFrame getCouponFrame(final Long couponFrameId) {
		return couponFrameRepository.findById(couponFrameId)
			.orElseThrow(NotFoundCouponFrameException::new);
	}
}
