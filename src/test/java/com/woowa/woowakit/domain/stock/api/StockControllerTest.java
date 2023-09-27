package com.woowa.woowakit.domain.stock.api;

import static com.woowa.woowakit.restDocsHelper.RestDocsHelper.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowa.woowakit.domain.stock.application.StockService;
import com.woowa.woowakit.domain.stock.dto.request.StockCreateRequest;
import com.woowa.woowakit.restDocsHelper.PathParam;
import com.woowa.woowakit.restDocsHelper.RequestFields;
import com.woowa.woowakit.restDocsHelper.RestDocsTest;

@WebMvcTest(StockController.class)
@AutoConfigureRestDocs(uriHost = "api.test.com", uriPort = 80)
@ExtendWith(RestDocumentationExtension.class)
class StockControllerTest extends RestDocsTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StockService stockService;

	@Autowired
	private ObjectMapper autowiredObjectMapper;

	@Test
	@DisplayName("[POST] [/stocks/{id}] 상품 재고 추가 테스트 및 문서화")
	void addStock() throws Exception {
		PathParam pathParam = new PathParam("id", "재고 ID");
		RequestFields requestFields = new RequestFields(Map.of(
			"expiryDate", "유통 기한",
			"quantity", "재고 수량"
		));

		Long stockId = 1L;
		String token = getToken();
		StockCreateRequest request = StockCreateRequest.of(LocalDate.of(2025, 1, 1), 10000L);
		given(stockService.create(any(), any())).willReturn(stockId);

		mockMvc.perform(post("/stocks/{id}", stockId)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(autowiredObjectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(handler().methodName("addStock"))
			.andDo(authorizationDocument("stock/add", pathParam, requestFields));
	}
}
