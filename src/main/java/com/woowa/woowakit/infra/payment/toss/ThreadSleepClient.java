package com.woowa.woowakit.infra.payment.toss;

import java.time.Duration;
import java.util.Random;

import com.woowa.woowakit.domain.order.domain.PaymentClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class ThreadSleepClient implements PaymentClient {

	private static final double LATENCY_MIN = 1.3;
	private static final double STANDARD_DEVIATION = 0.2;

	@Override
	public Mono<Void> validatePayment(
		final String paymentKey,
		final String orderToken,
		final Long totalPrice
	) {
		long latencyMs = getLatencyMs();

		return Mono.delay(Duration.ofMillis(latencyMs))
			.log("[mono]mono delay에 " + latencyMs + " ms가 수행되었습니다. paymentKey: " + paymentKey)
			.then();
	}

	private long getLatencyMs() {
		return (long)((LATENCY_MIN + STANDARD_DEVIATION * new Random().nextGaussian()) * 1000);
	}
}
