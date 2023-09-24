package com.woowa.woowakit.domain.order.domain;

import static com.woowa.woowakit.domain.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.cart.domain.CartItemRepository;
import com.woowa.woowakit.domain.order.domain.mapper.CartItemMapper;
import com.woowa.woowakit.domain.order.fixture.OrderFixture;
import com.woowa.woowakit.domain.product.domain.product.Product;
import com.woowa.woowakit.domain.product.domain.product.ProductRepository;

class PayResultHandlerTest {

	@Test
	@DisplayName("rollback 시 담은 개수만큼 다시 복구되는지 테스트.")
	void rollback() {
		// given
		PayResultHandler payResultHandler = new PayResultHandler(
			mock(CartItemRepository.class),
			mock(ProductRepository.class),
			mock(OrderRepository.class),
			mock(PaymentSaveService.class),
			mock(CartItemMapper.class)
		);
		Order order = OrderFixture.anOrder().build();
		Product product = getProduct();

		// when
		payResultHandler.rollbackProducts(order, List.of(product));

		// then
		assertThat(product).extracting("quantity").extracting("value").isEqualTo(2L);
	}

	private Product getProduct() {
		return getProductBuilder().id(1L).quantity(1).build();
	}
}
