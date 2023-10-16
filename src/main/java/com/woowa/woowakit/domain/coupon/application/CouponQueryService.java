package com.woowa.woowakit.domain.coupon.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.exception.NotFoundCouponException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponQueryService {

	private final CouponRepository couponRepository;

	public Coupon getCoupon(final Long couponId) {
		return couponRepository.findById(couponId)
			.orElseThrow(NotFoundCouponException::new);
	}
}
