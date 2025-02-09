package com.woowa.woowakit.domain.product.dto.response;

import com.woowa.woowakit.domain.product.domain.Product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProductDetailResponse {

	private Long id;
	private String name;
	private Long price;
	private String imageUrl;
	private String status;
	private long quantity;

	@Builder
	private ProductDetailResponse(
		final Long id,
		final String name,
		final Long price,
		final String imageUrl,
		final String status,
		final long quantity
	) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.imageUrl = imageUrl;
		this.status = status;
		this.quantity = quantity;
	}

	public static ProductDetailResponse from(final Product product) {
		return ProductDetailResponse.builder()
			.id(product.getId())
			.name(product.getName())
			.price(product.getPrice())
			.imageUrl(product.getImageUrl())
			.quantity(product.getQuantity())
			.status(product.getStatus().name())
			.build();
	}
}
