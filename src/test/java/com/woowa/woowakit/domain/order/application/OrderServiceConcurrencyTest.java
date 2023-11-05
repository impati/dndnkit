package com.woowa.woowakit.domain.order.application;

import static com.woowa.woowakit.domain.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.woowa.woowakit.domain.auth.domain.AuthPrincipal;
import com.woowa.woowakit.domain.auth.domain.Member;
import com.woowa.woowakit.domain.auth.domain.MemberRepository;
import com.woowa.woowakit.domain.member.fixture.MemberFixture;
import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderRepository;
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
	private OrderCommandService orderCommandService;

	@Autowired
	private OrderRepository orderRepository;

	@MockBean
	private PaymentClient paymentClient;

	@AfterEach
	void tearDown() {
		memberRepository.deleteAll();
		productRepository.deleteAll();
		orderRepository.deleteAll();
	}

	@Test
	@DisplayName("주문 동시성 테스트")
	void orderConcurrencyTest() throws Exception {
		Member member = memberRepository.save(MemberFixture.anMember().build());
		Product product = productRepository.save(getInStockProductBuilder().quantity(100L).build());
		LocalDate now = LocalDate.of(3023, 10, 22);

		int threadCount = 10;
		for (int i = 0; i < threadCount; i++) {
			List<OrderCreateRequest> request = List.of(OrderCreateRequest.of(product.getId(), 10L));
			orderCommandService.create(AuthPrincipal.from(member), request, now);
		}
		when(paymentClient.validatePayment(any(), any(), any())).thenReturn(Mono.empty());

		// when
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		List<Order> orders = orderRepository.findAll();
		for (Order order : orders) {
			OrderPayRequest request = OrderPayRequest.of("test", List.of());

			executorService.submit(() -> {
				try {
					orderCommandService.pay(AuthPrincipal.from(member), order.getId(), request);
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
}
