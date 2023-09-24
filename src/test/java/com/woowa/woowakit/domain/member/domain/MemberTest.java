package com.woowa.woowakit.domain.member.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.auth.domain.Email;
import com.woowa.woowakit.domain.auth.domain.EncodedPassword;
import com.woowa.woowakit.domain.auth.domain.Member;
import com.woowa.woowakit.domain.auth.domain.PasswordEncoder;
import com.woowa.woowakit.domain.auth.domain.Role;
import com.woowa.woowakit.domain.auth.exception.LoginFailException;
import com.woowa.woowakit.domain.auth.infra.PBKDF2PasswordEncoder;

class MemberTest {

	@Test
	@DisplayName("패스워드가 일치하지 않으면 예외를 던진다.")
	void validatePassword() {
		Member member = Member.builder()
			.role(Role.USER)
			.email(Email.from("yongs@naver.com"))
			.encodedPassword(EncodedPassword.of("12341aa11", new PBKDF2PasswordEncoder()))
			.name("yong")
			.build();

		PasswordEncoder passwordEncoder = new PasswordEncoder() {
			@Override
			public boolean matches(String planePassword, String encodedPassword) {
				return false;
			}

			@Override
			public String encode(String password) {
				return "1234111";
			}
		};

		assertThatThrownBy(() -> member.validatePassword("43222221", passwordEncoder))
			.isInstanceOf(LoginFailException.class);
	}
}
