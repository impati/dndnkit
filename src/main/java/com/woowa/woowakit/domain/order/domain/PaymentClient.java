package com.woowa.woowakit.domain.order.domain;

import reactor.core.publisher.Mono;

public interface PaymentClient {

	Mono<Void> validatePayment(
		final String paymentKey,
		final String orderToken,
		final Long totalPrice
	);
}
