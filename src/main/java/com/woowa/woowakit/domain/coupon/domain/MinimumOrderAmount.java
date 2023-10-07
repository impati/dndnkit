package com.woowa.woowakit.domain.coupon.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

import com.woowa.woowakit.domain.coupon.exception.MinimumOrderAmountException;

import lombok.Getter;

@Embeddable
@Getter
public class MinimumOrderAmount {

	private static final int MINIMUM_VALUE = 1000;

	private int value;

	protected MinimumOrderAmount() {
	}

	private MinimumOrderAmount(final int value) {
		validate(value);
		this.value = value;
	}

	private void validate(final int value) {
		if (value < MINIMUM_VALUE) {
			throw new MinimumOrderAmountException();
		}
	}

	public static MinimumOrderAmount from(final int value) {
		return new MinimumOrderAmount(value);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof MinimumOrderAmount)) {
			return false;
		}

		final MinimumOrderAmount that = (MinimumOrderAmount)o;
		return value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
