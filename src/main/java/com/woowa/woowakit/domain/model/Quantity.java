package com.woowa.woowakit.domain.model;

import java.util.Objects;

import javax.persistence.Embeddable;

import com.woowa.woowakit.domain.model.exception.QuantityNegativeException;

import lombok.Getter;

@Getter
@Embeddable
public class Quantity {

	private long value;

	protected Quantity() {
	}

	private Quantity(final long value) {
		validNotNegative(value);
		this.value = value;
	}

	public static Quantity from(final long quantity) {
		return new Quantity(quantity);
	}

	private void validNotNegative(final long quantity) {
		if (quantity < 0) {
			throw new QuantityNegativeException();
		}
	}

	public Quantity add(final long quantity) {
		return Quantity.from(value + quantity);
	}

	public Quantity subtract(final Quantity other) {
		return Quantity.from(value - other.value);
	}

	public boolean smallerThan(final Quantity other) {
		return value < other.value;
	}

	public boolean smallerThanOrEqualTo(final Quantity other) {
		return value <= other.value;
	}

	public boolean isEmpty() {
		return this.value == 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Quantity that = (Quantity)o;
		return value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "Quantity{" +
			"value=" + value +
			'}';
	}
}
