package com.woowa.woowakit.domain.fixture;

import com.woowa.woowakit.domain.product.domain.product.Product;
import com.woowa.woowakit.domain.product.domain.product.ProductStatus;

public class ProductFixture {

	public static Product.ProductBuilder getProductBuilder() {
		return Product.builder()
			.name("우아한 밀키트")
			.price(10000L)
			.imageUrl("woowakit.img")
			.quantity(100L)
			.status(ProductStatus.IN_STOCK);
	}
}
