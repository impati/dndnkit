package com.woowa.woowakit.domain.coupon.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

import com.woowa.woowakit.domain.coupon.exception.DiscountRateException;

@Embeddable
public class DiscountRate extends Discount {

	private static final int MINIMUM_VALUE = 0;
	private static final int MAXIMUM_VALUE = 100;

	protected DiscountRate() {
	}

	private DiscountRate(final int value) {
		validate(value);
		this.value = value;
	}

	private void validate(final int value) {
		if (value <= MINIMUM_VALUE || value > MAXIMUM_VALUE) {
			throw new DiscountRateException();
		}
	}

	public static DiscountRate from(final int value) {
		return new DiscountRate(value);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DiscountRate)) {
			return false;
		}

		final DiscountRate that = (DiscountRate)o;
		return value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
