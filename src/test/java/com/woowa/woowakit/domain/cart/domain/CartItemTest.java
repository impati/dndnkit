package com.woowa.woowakit.domain.cart.domain;

import static com.woowa.woowakit.domain.fixture.CartItemFixture.*;
import static com.woowa.woowakit.domain.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.cart.exception.CartItemQuantityException;
import com.woowa.woowakit.domain.cart.exception.InvalidProductInCartItemException;
import com.woowa.woowakit.domain.product.domain.product.Product;
import com.woowa.woowakit.domain.product.domain.product.ProductStatus;

class CartItemTest {

	@Test
	@DisplayName("cartItem 에 수량을 더할 수 있다.")
	void addQuantity() {
		Product product = getProduct(100, ProductStatus.IN_STOCK);
		CartItem cartItem = getCartItem(product, 10);

		cartItem.addQuantity(9);

		assertThat(cartItem.getQuantity()).isEqualTo(19L);
	}

	@Test
	@DisplayName("상품 상태가 판매 중이 아니라면 cartItem 에 수량을 더할 수 없다.")
	void addQuantityFailBecauseOfProductStatus() {
		Product product = getProduct(100, ProductStatus.STOPPED);
		CartItem cartItem = getCartItem(product, 10);

		assertThatCode(() -> cartItem.addQuantity(20))
			.isInstanceOf(InvalidProductInCartItemException.class)
			.hasMessage("상품을 구매할 수 없는 상태입니다.");
	}

	@Test
	@DisplayName("상품 수량이 부족하다면 cartItem 에 수량을 더할 수 없다.")
	void addQuantityFailBecauseOfQuantity() {
		Product product = getProduct(10, ProductStatus.IN_STOCK);
		CartItem cartItem = getCartItem(product, 10);

		assertThatCode(() -> cartItem.addQuantity(2))
			.isInstanceOf(CartItemQuantityException.class)
			.hasMessage("상품 수량보다 많은 수량을 장바구니에 담을 수 없습니다.");
	}

	@Test
	@DisplayName("상품 수량이 부족하다면 cartItem 에 수량을 업데이트 할 수 없다.")
	void updateQuantityFailBecauseOfQuantity() {
		Product product = getProduct(10, ProductStatus.IN_STOCK);
		CartItem cartItem = getCartItem(product, 10);

		assertThatCode(() -> cartItem.updateQuantity(11))
			.isInstanceOf(CartItemQuantityException.class)
			.hasMessage("상품 수량보다 많은 수량을 장바구니에 담을 수 없습니다.");
	}

	@Test
	@DisplayName("현재 사용자 cartItem 이라면 true 를 반환한다.")
	void isMyCartItem() {
		Long memberId = 1L;
		CartItem cartItem = getCartItem(memberId);

		assertThat(cartItem.isMyCartItem(memberId)).isTrue();
	}

	@Test
	@DisplayName("현재 사용자 cartItem 이 아니라면 false 를 반환한다.")
	void isMyCartItemFail() {
		Long memberId = 1L;
		CartItem cartItem = getCartItem(memberId);

		assertThat(cartItem.isMyCartItem(-1L)).isFalse();
	}

	@Test
	@DisplayName("cartItem 를 처음 생성하면 수량이 0이다.")
	void createCartItem() {
		CartItem cartItem = getCartItem();

		assertThat(cartItem.getQuantity()).isZero();
	}

	private Product getProduct(final long quantity, final ProductStatus productStatus) {
		return getProductBuilder()
			.quantity(quantity)
			.status(productStatus)
			.build();
	}

	private CartItem getCartItem(final Product product, final long quantity) {
		return getCartItemBuilder()
			.product(product)
			.quantity(quantity)
			.build();
	}

	private CartItem getCartItem(final Long memberId) {
		return getCartItemBuilder()
			.memberId(memberId)
			.build();
	}

	private CartItem getCartItem() {
		return getCartItemBuilder()
			.build();
	}
}
