package com.woowa.woowakit.domain.product.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

import com.woowa.woowakit.domain.model.Money;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
@Getter
public class ProductPrice {

	private Money price;

	protected ProductPrice() {
	}

	public static ProductPrice from(final Money value) {
		return new ProductPrice(value);
	}

	public static ProductPrice from(final Long value) {
		return new ProductPrice(Money.from(value));
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ProductPrice)) {
			return false;
		}

		final ProductPrice that = (ProductPrice)o;
		return Objects.equals(price, that.price);
	}

	@Override
	public int hashCode() {
		return Objects.hash(price);
	}
}
