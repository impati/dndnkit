package com.woowa.woowakit.domain.auth.api;

import static com.woowa.woowakit.restDocsHelper.RestDocsHelper.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.woowa.woowakit.domain.auth.application.AuthService;
import com.woowa.woowakit.domain.auth.dto.request.LoginRequest;
import com.woowa.woowakit.domain.auth.dto.request.SignUpRequest;
import com.woowa.woowakit.domain.auth.dto.response.LoginResponse;
import com.woowa.woowakit.restDocsHelper.RequestFields;
import com.woowa.woowakit.restDocsHelper.ResponseFields;
import com.woowa.woowakit.restDocsHelper.RestDocsTest;

@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs(uriHost = "api.test.com", uriPort = 80)
@ExtendWith(RestDocumentationExtension.class)
class AuthControllerTest extends RestDocsTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@Test
	@DisplayName("[POST] [/auth/signup] 회원가입 테스트 및 문서화")
	void signUp() throws Exception {
		RequestFields requestFields = new RequestFields(Map.of(
			"name", "이름",
			"email", "이메일",
			"password", "비밀번호"
		));
		ResponseFields responseFields = new ResponseFields(Map.of(
			"id", "회원 ID"
		));

		SignUpRequest request = SignUpRequest.of("yongs170@naver.com", "test1234", "tester");
		given(authService.signUp(any())).willReturn(1L);

		mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(handler().methodName("signUp"))
			.andDo(defaultDocument("auth/signup", requestFields, responseFields));
	}

	@Test
	@DisplayName("[POST] [/auth/login] 로그인 테스트 및 문서화")
	void login() throws Exception {
		RequestFields requestFields = new RequestFields(Map.of(
			"email", "이메일",
			"password", "비밀번호"
		));
		ResponseFields responseFields = new ResponseFields(Map.of(
			"accessToken", "유저 토큰",
			"name", "이름",
			"role", "역할",
			"email", "이메일"
		));

		LoginRequest request = LoginRequest.of("yongs170@naver.com", "test1234");
		LoginResponse response = LoginResponse.builder()
			.name("tester")
			.role("ADMIN")
			.accessToken("asadfdsewdqw")
			.email("yongs170@naver.com")
			.build();
		given(authService.loginMember(any())).willReturn(response);

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(handler().methodName("login"))
			.andDo(MockMvcResultHandlers.print())
			.andDo(defaultDocument("auth/login", requestFields, responseFields));
	}
}
