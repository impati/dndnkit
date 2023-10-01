package com.woowa.woowakit.domain.order.domain;

import org.springframework.stereotype.Component;

import com.woowa.woowakit.domain.order.exception.InvalidPayRequestException;
import com.woowa.woowakit.domain.order.exception.OrderNotFoundException;
import com.woowa.woowakit.domain.order.exception.PayFailedException;

import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPayService {

	private final PaymentClient paymentClient;
	private final OrderRepository orderRepository;
	private final PayResultHandler payResultHandler;

	@Counted("order.payment.request")
	public void pay(final Long orderId, final String paymentKey) {
		final Order order = getOrder(orderId);

		paymentClient.validatePayment(paymentKey, order.getUuid(), order.getTotalPrice())
			.publishOn(Schedulers.boundedElastic())
			.doOnSuccess(ignore -> payResultHandler.save(orderId, paymentKey))
			.doOnError(error -> payResultHandler.rollback(orderId, error))
			.onErrorMap(IllegalArgumentException.class, InvalidPayRequestException::new)
			.onErrorMap(IllegalStateException.class, PayFailedException::new)
			.block();
	}

	private Order getOrder(final Long orderId) {
		return orderRepository.findById(orderId)
			.orElseThrow(OrderNotFoundException::new);
	}
}
