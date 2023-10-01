package com.woowa.woowakit.domain.product.domain;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.woowa.woowakit.domain.model.BaseEntity;
import com.woowa.woowakit.domain.model.Quantity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_sales")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductSales extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "product_id")
	private Long productId;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "sale"))
	private Quantity sale;

	@Column(name = "sale_date")
	private LocalDate saleDate;

	@Builder
	public ProductSales(
		final Long productId,
		final long sale,
		final LocalDate saleDate
	) {
		this.productId = productId;
		this.sale = Quantity.from(sale);
		this.saleDate = saleDate;
	}

	public long getSale() {
		return sale.getValue();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ProductSales)) {
			return false;
		}

		final ProductSales that = (ProductSales)o;
		return this.getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
