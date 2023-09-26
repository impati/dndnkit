package com.woowa.woowakit.domain.payment.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.order.domain.PaymentSaveService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentSaveServiceImpl implements PaymentSaveService {

	private final PaymentRepository paymentRepository;

	@Transactional
	public void save(final Long orderId, final long totalPrice, final String paymentKey) {
		Payment payment = Payment.builder()
			.paymentKey(paymentKey)
			.orderId(orderId)
			.totalPrice(totalPrice)
			.build();

		paymentRepository.save(payment);
	}
}
