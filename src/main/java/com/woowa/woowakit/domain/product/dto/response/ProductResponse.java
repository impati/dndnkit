package com.woowa.woowakit.domain.product.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductSpecification;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProductResponse {

	private Long id;
	private String name;
	private Long price;
	private String imageUrl;
	private String status;
	private long quantity;
	private long productSale;

	@Builder
	private ProductResponse(
		final Long id,
		final String name,
		final Long price,
		final String imageUrl,
		final String status,
		final long quantity,
		final long productSale
	) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.imageUrl = imageUrl;
		this.status = status;
		this.quantity = quantity;
		this.productSale = productSale;
	}

	public static ProductResponse from(final ProductSpecification productSpecification) {
		return ProductResponse.builder()
			.id(productSpecification.getProduct().getId())
			.name(productSpecification.getProduct().getName())
			.price(productSpecification.getProduct().getPrice())
			.imageUrl(productSpecification.getProduct().getImageUrl())
			.quantity(productSpecification.getProduct().getQuantity())
			.status(productSpecification.getProduct().getStatus().name())
			.productSale(productSpecification.getProductSale())
			.build();
	}

	public static ProductResponse from(final Product product) {
		return ProductResponse.builder()
			.id(product.getId())
			.name(product.getName())
			.price(product.getPrice())
			.imageUrl(product.getImageUrl())
			.quantity(product.getQuantity())
			.status(product.getStatus().name())
			.build();
	}

	public static List<ProductResponse> listOfProductSpecification(
		final List<ProductSpecification> productSpecifications
	) {
		return productSpecifications.stream()
			.map(ProductResponse::from)
			.collect(Collectors.toUnmodifiableList());
	}

	public static List<ProductResponse> listOfProducts(final List<Product> products) {
		return products.stream()
			.map(ProductResponse::from)
			.collect(Collectors.toUnmodifiableList());
	}
}
