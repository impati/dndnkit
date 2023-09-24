package com.woowa.woowakit.domain.auth.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.woowa.woowakit.domain.auth.exception.PasswordInvalidException;

import lombok.Getter;

@Getter
@Embeddable
public class EncodedPassword {

	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.{7,18}$).*";

	@Column(name = "password", nullable = false)
	private String value;

	protected EncodedPassword() {
	}

	private EncodedPassword(final String encodedPassword) {
		this.value = encodedPassword;
	}

	public static EncodedPassword of(final String rawPassword, final PasswordEncoder passwordEncoder) {
		validate(rawPassword);
		return new EncodedPassword(passwordEncoder.encode(rawPassword));
	}

	private static void validate(final String rawPassword) {
		if (!rawPassword.matches(PASSWORD_REGEX)) {
			throw new PasswordInvalidException();
		}
	}

	public boolean isMatch(final String rawPassword, final PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(rawPassword, value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof EncodedPassword)) {
			return false;
		}
		EncodedPassword that = (EncodedPassword)o;
		return Objects.equals(value, that.value);
	}

	@Override
	public String toString() {
		return "Password{" +
			"value='" + value + '\'' +
			'}';
	}
}
