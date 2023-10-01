package com.woowa.woowakit.domain.auth.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.util.StringUtils;

import lombok.Getter;

@Getter
@Embeddable
public class MemberName {

	@Column(name = "name", nullable = false, length = 255)
	private String value;

	protected MemberName() {
	}

	private MemberName(final String value) {
		validate(value);
		this.value = value;
	}

	private void validate(final String value) {
		if (!(StringUtils.hasText(value) && value.length() <= 255)) {
			throw new MemberNameException();
		}
	}

	public static MemberName from(final String name) {
		return new MemberName(name);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof MemberName)) {
			return false;
		}

		final MemberName that = (MemberName)o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "Email{" +
			"value='" + value + '\'' +
			'}';
	}
}
