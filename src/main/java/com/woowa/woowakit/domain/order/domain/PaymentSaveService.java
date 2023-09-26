package com.woowa.woowakit.domain.order.domain;

public interface PaymentSaveService {

	void save(
		final Long orderId,
		final long totalPrice,
		final String paymentKey
	);
}
