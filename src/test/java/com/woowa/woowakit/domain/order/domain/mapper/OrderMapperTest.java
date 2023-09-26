package com.woowa.woowakit.domain.order.domain.mapper;

import static com.woowa.woowakit.domain.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderItem;
import com.woowa.woowakit.domain.order.domain.OrderStatus;
import com.woowa.woowakit.domain.order.dto.request.OrderCreateRequest;
import com.woowa.woowakit.domain.order.exception.ProductNotFoundException;
import com.woowa.woowakit.domain.order.exception.ProductNotOnSaleException;
import com.woowa.woowakit.domain.order.exception.QuantityNotEnoughException;
import com.woowa.woowakit.domain.product.domain.product.Product;
import com.woowa.woowakit.domain.product.domain.product.ProductRepository;
import com.woowa.woowakit.domain.product.domain.product.ProductStatus;

@DisplayName("OrderMapper 단위 테스트")
@SpringBootTest
class OrderMapperTest {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderMapper orderMapper;

	@Test
	@DisplayName("사용자 정보와 주문 요청 정보들을 받아 주문을 생성한다.")
	void mapFrom() {
		Product productA = getProduct(1000L, "productA");
		Long memberId = 1L;
		List<OrderCreateRequest> request = List.of(OrderCreateRequest.of(productA.getId(), 10L));

		Order order = orderMapper.mapFrom(memberId, request);

		assertThat(order.getMemberId()).isEqualTo(memberId);
		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDERED);
		assertThat(order.getOrderItems()).hasSize(1)
			.extracting(OrderItem::getName, OrderItem::getPrice)
			.contains(tuple("productA", 1000L));
	}

	@Test
	@DisplayName("주문 요청 정보가 없으면 주문 생성에 실패한다.")
	void mapFromFailBecauseOfRequest() {
		Long memberId = 1L;

		assertThatCode(() -> orderMapper.mapFrom(memberId, List.of()))
			.isInstanceOf(ProductNotFoundException.class)
			.hasMessage("존재하지 않은 상품 정보입니다.");
	}

	@Test
	@DisplayName("주문 요청 정보에 맞는 상품이 없으면 주문 생성에 실패한다.")
	void mapFromFailBecauseOfProduct() {
		Long memberId = 1L;
		List<OrderCreateRequest> request = List.of(OrderCreateRequest.of(-1L, 10L));

		assertThatCode(() -> orderMapper.mapFrom(memberId, request))
			.isInstanceOf(ProductNotFoundException.class)
			.hasMessage("존재하지 않은 상품 정보입니다.");
	}

	@Test
	@DisplayName("주문 요청 정보에 맞는 상품이 판매중이 아니면 주문 생성에 실패한다.")
	void mapFromFailBecauseOfProductStatus() {
		Product product = getSoldOutProduct();
		Long memberId = 1L;
		List<OrderCreateRequest> request = List.of(OrderCreateRequest.of(product.getId(), 10L));

		assertThatCode(() -> orderMapper.mapFrom(memberId, request))
			.isInstanceOf(ProductNotOnSaleException.class)
			.hasMessage("판매 중이 아닌 상품입니다.");
	}

	@Test
	@DisplayName("주문 요청 정보에 맞는 상품 수량보다 더 큰 수량 주문을 시도하면 주문 생성에 실패한다.")
	void mapFromFailBecauseOfQuantity() {
		long quantity = 100;
		Product product = getProduct(quantity);
		Long memberId = 1L;
		List<OrderCreateRequest> request = List.of(OrderCreateRequest.of(product.getId(), 1000L));

		assertThatCode(() -> orderMapper.mapFrom(memberId, request))
			.isInstanceOf(QuantityNotEnoughException.class)
			.hasMessage("상품의 재고가 부족합니다");
	}

	private Product getSoldOutProduct() {
		return productRepository.save(getProductBuilder()
			.status(ProductStatus.SOLD_OUT)
			.build());
	}

	private Product getProduct(final long quantity) {
		return productRepository.save(getProductBuilder()
			.quantity(quantity)
			.status(ProductStatus.IN_STOCK)
			.build());
	}

	private Product getProduct(
		final long price,
		final String productName
	) {
		return productRepository.save(getProductBuilder()
			.price(price)
			.name(productName)
			.quantity(100)
			.status(ProductStatus.IN_STOCK)
			.build());
	}
}
