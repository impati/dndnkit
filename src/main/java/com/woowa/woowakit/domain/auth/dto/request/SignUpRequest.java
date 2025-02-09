package com.woowa.woowakit.domain.auth.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SignUpRequest {

	@Email
	@NotNull
	private String email;

	@NotBlank
	private String password;

	@NotBlank
	private String name;

	public static SignUpRequest of(
		final String email,
		final String password,
		final String name
	) {
		return new SignUpRequest(email, password, name);
	}
}
