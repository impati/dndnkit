package com.woowa.woowakit.domain.fixture;

import com.woowa.woowakit.domain.cart.domain.CartItem;

public class CartItemFixture {

	public static CartItem.CartItemBuilder getCartItemBuilder() {
		return CartItem.builder()
			.memberId(1L);
	}
}
