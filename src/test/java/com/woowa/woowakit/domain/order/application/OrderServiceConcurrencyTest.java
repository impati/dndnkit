package com.woowa.woowakit.domain.order.application;

import static com.woowa.woowakit.domain.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.woowa.woowakit.domain.auth.domain.AuthPrincipal;
import com.woowa.woowakit.domain.auth.domain.Member;
import com.woowa.woowakit.domain.auth.domain.MemberRepository;
import com.woowa.woowakit.domain.member.fixture.MemberFixture;
import com.woowa.woowakit.domain.order.domain.PaymentClient;
import com.woowa.woowakit.domain.order.dto.request.OrderCreateRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderPayRequest;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;

import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("OrderService 동시성 테스트")
class OrderServiceConcurrencyTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderService orderService;

	@MockBean
	private PaymentClient paymentClient;

	@Test
	@DisplayName("주문 동시성 테스트")
	void orderConcurrencyTest() throws Exception {
		Member member = memberRepository.save(MemberFixture.anMember().build());
		Product product = productRepository.save(getProduct());

		int threadCount = 10;
		for (int i = 0; i < threadCount; i++) {
			List<OrderCreateRequest> request = List.of(OrderCreateRequest.of(product.getId(), 10L));
			orderService.create(AuthPrincipal.from(member), request);
		}
		when(paymentClient.validatePayment(any(), any(), any())).thenReturn(Mono.empty());

		// when
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			long orderId = i + 1;
			OrderPayRequest request = OrderPayRequest.of("test");

			executorService.submit(() -> {
				try {
					orderService.pay(AuthPrincipal.from(member), orderId, request);
				} finally {
					countDownLatch.countDown();
				}
			});
		}
		countDownLatch.await();
		productRepository.flush();

		// then
		Product afterProduct = productRepository.findById(product.getId()).orElseThrow();
		assertThat(afterProduct.getQuantity()).isZero();
	}

	private static Product getProduct() {
		return getInStockProductBuilder().build();
	}
}
