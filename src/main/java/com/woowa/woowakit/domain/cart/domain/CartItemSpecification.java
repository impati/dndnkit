package com.woowa.woowakit.domain.cart.domain;

import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.domain.ProductImage;
import com.woowa.woowakit.domain.product.domain.ProductName;
import com.woowa.woowakit.domain.product.domain.ProductPrice;

import lombok.Getter;

@Getter
public class CartItemSpecification {

	private final long quantity;
	private final long productPrice;
	private final String productName;
	private final String productImage;
	private final Long productId;
	private final Long cartItemId;

	public CartItemSpecification(
		final Quantity quantity,
		final ProductPrice productPrice,
		final ProductName productName,
		final ProductImage productImage,
		final Long productId,
		final Long cartItemId
	) {
		this.quantity = quantity.getValue();
		this.productPrice = productPrice.getPrice().getValue();
		this.productName = productName.getValue();
		this.productImage = productImage.getValue();
		this.productId = productId;
		this.cartItemId = cartItemId;
	}
}
