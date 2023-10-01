package com.woowa.woowakit.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {

	private final String accessToken;
	private final String name;
	private final String role;
	private final String email;

	@Builder
	private LoginResponse(
		final String accessToken,
		final String name,
		final String role,
		final String email
	) {
		this.accessToken = accessToken;
		this.name = name;
		this.role = role;
		this.email = email;
	}
}
