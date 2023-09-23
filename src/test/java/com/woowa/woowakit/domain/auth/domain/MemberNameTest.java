package com.woowa.woowakit.domain.auth.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MemberNameTest {

	@ParameterizedTest
	@MethodSource("nameArgs")
	@DisplayName("사용자 닉네임이 한글자 이상 255글자 이하이면 생성하는데 성공한다.")
	void memberNameValidateTest(final String value) {
		MemberName memberName = MemberName.from(value);

		assertThat(memberName.getValue()).isEqualTo(value);
	}

	@ParameterizedTest
	@MethodSource("nameFailArgs")
	@DisplayName("사용자 닉네임이 없거나 255글자 초과이면 생성하는데 실패한다.")
	void memberNameValidateFailTest(final String value) {
		assertThatCode(() -> MemberName.from(value))
			.isInstanceOf(MemberNameException.class)
			.hasMessage("사용자 닉네임은 한글자 이상 255글자 이하여야합니다.");
	}

	static Stream<Arguments> nameArgs() {
		return Stream.of(
			arguments("1"),
			arguments("abc"),
			arguments("안".repeat(255))
		);
	}

	static Stream<Arguments> nameFailArgs() {
		return Stream.of(
			arguments(""),
			arguments("a".repeat(256))
		);
	}
}
