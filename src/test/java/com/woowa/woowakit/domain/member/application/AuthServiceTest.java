package com.woowa.woowakit.domain.member.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.auth.application.AuthService;
import com.woowa.woowakit.domain.auth.domain.MemberNameException;
import com.woowa.woowakit.domain.auth.dto.request.LoginRequest;
import com.woowa.woowakit.domain.auth.dto.request.SignUpRequest;
import com.woowa.woowakit.domain.auth.exception.EmailInvalidException;
import com.woowa.woowakit.domain.auth.exception.LoginFailException;
import com.woowa.woowakit.domain.auth.exception.PasswordInvalidException;

@SpringBootTest
class AuthServiceTest {

	@Autowired
	private AuthService authService;

	@Test
	@DisplayName("없는 이메일로 조회하면 예외를 반환한다.")
	void notFoundEmail() {
		assertThatCode(() -> authService.loginMember(LoginRequest.of("zzz@woowa.com", "asdfasdfasdf")))
			.isInstanceOf(LoginFailException.class);
	}

	@Test
	@DisplayName("유효하지 않는 비밀번호로 회원가입 시도 시 예외를 반환한다.")
	void invalidPassword() {
		assertThatCode(() -> authService.signUp(SignUpRequest.of("yongs170@naver.com", "asdasd", "hello")))
			.isInstanceOf(PasswordInvalidException.class);
	}

	@Test
	@DisplayName("유효하지 않는 이메일로 회원가입 시도 시 예외를 반환한다.")
	void invalidEmail() {
		assertThatCode(() -> authService.signUp(SignUpRequest.of("yongs170naver.com", "asdasd12345", "hello")))
			.isInstanceOf(EmailInvalidException.class);
	}

	@Test
	@DisplayName("유효하지 않는 닉네임으로 회원가입 시도 시 예외를 반환한다.")
	void invalidName() {
		assertThatCode(() -> authService.signUp(SignUpRequest.of("yongs170@naver.com", "asdasd12345", "")))
			.isInstanceOf(MemberNameException.class);
	}
}
