package com.woowa.woowakit.domain.order.dto.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponAppliedOrderItemRequest {

	@NotNull
	private Long orderItemId;
	private Long couponId;

	public static CouponAppliedOrderItemRequest of(
		final Long orderItemId,
		final Long couponId
	) {
		return new CouponAppliedOrderItemRequest(orderItemId, couponId);
	}
}
