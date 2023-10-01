package com.woowa.woowakit.domain.auth.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.woowa.woowakit.domain.auth.exception.PasswordInvalidException;
import com.woowa.woowakit.domain.auth.infra.PBKDF2PasswordEncoder;

class EncodedPasswordTest {

	@ParameterizedTest
	@ValueSource(strings = {"a123456", "12345678912345678b", "abcdef123AS"})
	@DisplayName("비밀번호는 하나 이상의 소문자를 포함한 7글자 이상 18글자 이하여야 한다.")
	void validateEncodedPassword(final String value) {
		PBKDF2PasswordEncoder pbkdf2PasswordEncoder = new PBKDF2PasswordEncoder();

		assertThatCode(() -> EncodedPassword.of(value, pbkdf2PasswordEncoder))
			.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@ValueSource(strings = {"a12345", "1234567", "12345678912345678ba", "abcdef123AS123123BBDD"})
	@DisplayName("비밀번호는 하나 이상의 소문자를 포함한 7글자 이상 18글자가 아니면 비밀번호 인코딩에 실패한다.")
	void validateEncodedPasswordFail(final String value) {
		PBKDF2PasswordEncoder pbkdf2PasswordEncoder = new PBKDF2PasswordEncoder();

		assertThatCode(() -> EncodedPassword.of(value, pbkdf2PasswordEncoder))
			.isInstanceOf(PasswordInvalidException.class)
			.hasMessage("비밀번호는 하나 이상의 소문자를 포함한 7글자 이상 18글자 이하여야 합니다.");
	}

	@Test
	@DisplayName("비밀번호는 PBKDF2 암호화 방식으로 인코딩 해야한다.")
	void encodedPasswordByPBKDF2() {
		PBKDF2PasswordEncoder pbkdf2PasswordEncoder = new PBKDF2PasswordEncoder();
		EncodedPassword encodedPassword = EncodedPassword.of("a123456", pbkdf2PasswordEncoder);

		assertThat(encodedPassword.getValue()).isNotEqualTo("1234");
	}

	@Test
	@DisplayName("비밀번호가 일치하면 비밀번호 매치 검사하는데 성공한다.")
	void passwordMatch() {
		PBKDF2PasswordEncoder pbkdf2PasswordEncoder = new PBKDF2PasswordEncoder();
		EncodedPassword encodedPassword = EncodedPassword.of("a123456", pbkdf2PasswordEncoder);

		assertThat(encodedPassword.isMatch("a123456", pbkdf2PasswordEncoder)).isTrue();
	}

	@Test
	@DisplayName("비밀번호가 일치하지 않으면 비밀번호 매치 검사하는데 실패한다.")
	void passwordMatchFail() {
		PBKDF2PasswordEncoder pbkdf2PasswordEncoder = new PBKDF2PasswordEncoder();
		EncodedPassword encodedPassword = EncodedPassword.of("a123456", pbkdf2PasswordEncoder);

		assertThat(encodedPassword.isMatch("a123457", pbkdf2PasswordEncoder)).isFalse();
	}
}
