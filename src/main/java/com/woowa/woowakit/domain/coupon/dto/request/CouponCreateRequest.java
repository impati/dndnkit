package com.woowa.woowakit.domain.coupon.dto.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponCreateRequest {

	@NotNull(message = "쿠폰틀 ID 는 필수입니다.")
	private Long couponFrameId;

	public static CouponCreateRequest from(final Long couponFrameId) {
		return new CouponCreateRequest(couponFrameId);
	}
}
