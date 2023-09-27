package com.woowa.woowakit.domain.product.domain;

import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.woowa.woowakit.domain.model.BaseEntity;
import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.exception.UpdateProductStatusFailException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {

	private static final Quantity INITIAL_QUANTITY = Quantity.from(0);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "name"))
	private ProductName name;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "image_url"))
	private ProductImage imageUrl;

	@Embedded
	@AttributeOverride(name = "price.value", column = @Column(name = "price"))
	private ProductPrice price;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "quantity"))
	private Quantity quantity;

	@Enumerated(EnumType.STRING)
	private ProductStatus status;

	private Product(
		final Long id,
		final String name,
		final long price,
		final String imageUrl,
		final ProductStatus status,
		final long quantity
	) {
		this.id = id;
		this.name = ProductName.from(name);
		this.price = ProductPrice.from(price);
		this.imageUrl = ProductImage.from(imageUrl);
		this.status = status;
		this.quantity = Quantity.from(quantity);
	}

	public void updateProductStatus(final ProductStatus productStatus) {
		if (Objects.equals(quantity, INITIAL_QUANTITY)) {
			throw new UpdateProductStatusFailException();
		}

		this.status = productStatus;
	}

	public boolean isOnSale() {
		return status == ProductStatus.IN_STOCK;
	}

	public void addQuantity(final Quantity quantity) {
		this.quantity = this.quantity.add(quantity.getValue());
	}

	public void subtractQuantity(final Quantity quantity) {
		this.quantity = this.quantity.subtract(quantity);

		if (this.quantity.isEmpty()) {
			this.status = ProductStatus.SOLD_OUT;
		}
	}

	public boolean isSmallerThan(final long subtractExpiryQuantity) {
		return this.quantity.smallerThan(Quantity.from(subtractExpiryQuantity));
	}

	public boolean isEnoughQuantity(final Quantity requiredQuantity) {
		return requiredQuantity.smallerThanOrEqualTo(this.quantity);
	}

	public String getImageUrl() {
		return imageUrl.getValue();
	}

	public long getPrice() {
		return price.getPrice().getValue();
	}

	public String getName() {
		return name.getValue();
	}

	public long getQuantity() {
		return quantity.getValue();
	}

	public static ProductBuilder builder() {
		return new ProductBuilder();
	}

	public static class ProductBuilder {
		private Long id;
		private String name;
		private long price;
		private String imageUrl;
		private ProductStatus status = ProductStatus.PRE_REGISTRATION;
		private long quantity;

		ProductBuilder() {
		}

		public ProductBuilder id(final Long id) {
			this.id = id;
			return this;
		}

		public ProductBuilder name(final String name) {
			this.name = name;
			return this;
		}

		public ProductBuilder price(final long price) {
			this.price = price;
			return this;
		}

		public ProductBuilder imageUrl(final String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public ProductBuilder status(final ProductStatus status) {
			this.status = status;
			return this;
		}

		public ProductBuilder quantity(final long quantity) {
			this.quantity = quantity;
			return this;
		}

		public Product build() {
			return new Product(this.id, this.name, this.price, this.imageUrl, this.status, this.quantity);
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Product)) {
			return false;
		}

		final Product product = (Product)o;
		return this.getId() != null && Objects.equals(getId(), product.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
