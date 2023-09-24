package com.woowa.woowakit.domain.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowa.woowakit.domain.auth.domain.AuthPrincipal;
import com.woowa.woowakit.domain.auth.domain.Email;
import com.woowa.woowakit.domain.auth.domain.EncodedPassword;
import com.woowa.woowakit.domain.auth.domain.Member;
import com.woowa.woowakit.domain.auth.domain.MemberRepository;
import com.woowa.woowakit.domain.auth.domain.PasswordEncoder;
import com.woowa.woowakit.domain.auth.domain.Role;
import com.woowa.woowakit.domain.auth.dto.request.LoginRequest;
import com.woowa.woowakit.domain.auth.dto.request.SignUpRequest;
import com.woowa.woowakit.domain.auth.dto.response.LoginResponse;
import com.woowa.woowakit.domain.auth.exception.LoginFailException;
import com.woowa.woowakit.domain.auth.infra.TokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final ObjectMapper objectMapper;

	public Long signUp(final SignUpRequest request) {
		final Member member = Member.builder()
			.role(Role.USER)
			.email(Email.from(request.getEmail()))
			.encodedPassword(EncodedPassword.of(request.getPassword(), passwordEncoder))
			.name(request.getName())
			.build();

		return memberRepository.save(member).getId();
	}

	@Transactional(readOnly = true)
	public LoginResponse loginMember(final LoginRequest loginRequest) {
		final Member member = getMemberByEmail(loginRequest.getEmail());
		member.validatePassword(loginRequest.getPassword(), passwordEncoder);

		final String accessToken = createAccessToken(member);

		return LoginResponse.builder()
			.role(member.getRoleName())
			.accessToken(accessToken)
			.email(member.getEmail())
			.name(member.getName())
			.build();
	}

	private String createAccessToken(final Member member) {
		return tokenProvider.createToken(principalToJson(AuthPrincipal.from(member)));
	}

	private String principalToJson(final AuthPrincipal authPrincipal) {
		try {
			return objectMapper.writeValueAsString(authPrincipal);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	private Member getMemberByEmail(final String email) {
		return memberRepository.findByEmail(Email.from(email))
			.orElseThrow(LoginFailException::new);
	}
}
