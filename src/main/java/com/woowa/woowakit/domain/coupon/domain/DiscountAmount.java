package com.woowa.woowakit.domain.coupon.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

import com.woowa.woowakit.domain.coupon.exception.DiscountAmountException;

@Embeddable
public class DiscountAmount extends Discount {

	private static final int MINIMUM_VALUE = 0;

	protected DiscountAmount() {
	}

	private DiscountAmount(final int value, final int minimumAmount) {
		validate(value, minimumAmount);
		this.value = value;
	}

	private void validate(final int value, final int minimumAmount) {
		if (value <= MINIMUM_VALUE || value > minimumAmount) {
			throw new DiscountAmountException();
		}
	}

	public static DiscountAmount of(final int value, final int minimumAmount) {
		return new DiscountAmount(value, minimumAmount);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DiscountAmount)) {
			return false;
		}

		final DiscountAmount that = (DiscountAmount)o;
		return value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
