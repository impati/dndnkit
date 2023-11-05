package com.woowa.woowakit.domain.order.dto.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderPayRequest {

	@NotNull(message = "결제 키는 필수값입니다.")
	private String paymentKey;
	private List<CouponAppliedOrderItemRequest> couponAppliedOrderItems;

	public static OrderPayRequest of(
		final String paymentKey,
		final List<CouponAppliedOrderItemRequest> couponAppliedOrderItems
	) {
		return new OrderPayRequest(paymentKey, couponAppliedOrderItems);
	}
}
