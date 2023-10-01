package com.woowa.woowakit.domain.stock.domain;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

import javax.persistence.Embeddable;

import com.woowa.woowakit.domain.stock.exception.StockExpiredException;

import lombok.Getter;

@Getter
@Embeddable
public class ExpiryDate implements Comparable<ExpiryDate> {

	private LocalDate value;

	protected ExpiryDate() {
	}

	private ExpiryDate(final LocalDate date) {
		validExpiryDate(date);
		this.value = date;
	}

	public static ExpiryDate from(final LocalDate date) {
		return new ExpiryDate(date);
	}

	private void validExpiryDate(final LocalDate date) {
		if (date.isBefore(LocalDate.now(ZoneId.of("Asia/Seoul")))) {
			throw new StockExpiredException();
		}
	}

	@Override
	public int compareTo(final ExpiryDate o) {
		return this.value.compareTo(o.value);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ExpiryDate)) {
			return false;
		}

		final ExpiryDate that = (ExpiryDate)o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
