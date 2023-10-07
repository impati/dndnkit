package com.woowa.woowakit.domain.model;

import java.util.Objects;

import javax.persistence.Embeddable;

import com.woowa.woowakit.domain.model.exception.MoneyNegativeException;

import lombok.Getter;

@Getter
@Embeddable
public class Money {

	public static final Money ZERO = new Money(0L);

	private Long value;

	protected Money() {
	}

	private Money(final long value) {
		validate(value);
		this.value = value;
	}

	private void validate(final long value) {
		if (value < 0) {
			throw new MoneyNegativeException();
		}
	}

	public static Money from(final Long value) {
		return new Money(value);
	}

	public Money multiply(final Long value) {
		return new Money(this.value * value);
	}

	public Money add(final Money money) {
		return new Money(this.value + money.value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Money)) {
			return false;
		}

		Money money = (Money)o;
		return Objects.equals(value, money.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "Money{" +
			"value=" + value +
			'}';
	}
}
