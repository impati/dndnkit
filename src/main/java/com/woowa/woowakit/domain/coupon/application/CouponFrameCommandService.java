package com.woowa.woowakit.domain.coupon.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.coupon.domain.CouponFrame;
import com.woowa.woowakit.domain.coupon.domain.CouponFrameRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.dto.request.CouponFrameCreateRequest;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponFrameCommandService {

	private final CouponFrameRepository couponFrameRepository;

	public Long create(final CouponTarget couponTarget, final CouponFrameCreateRequest request) {
		final CouponFrame couponFrame = request.toEntity(couponTarget);

		return couponFrameRepository.save(couponFrame).getId();
	}
}
