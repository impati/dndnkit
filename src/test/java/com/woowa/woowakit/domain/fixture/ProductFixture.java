package com.woowa.woowakit.domain.fixture;

import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductStatus;

public class ProductFixture {

	public static Product.ProductBuilder getInStockProductBuilder() {
		return Product.builder()
			.name("우아한 밀키트")
			.price(10000L)
			.imageUrl("https://woowakit.img")
			.quantity(100L)
			.status(ProductStatus.IN_STOCK);
	}

	public static Product.ProductBuilder getProductBuilder() {
		return Product.builder()
			.name("우아한 밀키트")
			.price(10000L)
			.imageUrl("https://woowakit.img")
			.quantity(100L)
			.status(ProductStatus.PRE_REGISTRATION);
	}
}
