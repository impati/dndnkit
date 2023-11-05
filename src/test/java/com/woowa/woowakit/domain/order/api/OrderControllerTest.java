package com.woowa.woowakit.domain.order.api;

import static com.woowa.woowakit.restDocsHelper.RestDocsHelper.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;
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
import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.fixture.CouponFixture;
import com.woowa.woowakit.domain.order.application.OrderCommandService;
import com.woowa.woowakit.domain.order.application.OrderQueryService;
import com.woowa.woowakit.domain.order.domain.CouponGroupOrderItem;
import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderItem;
import com.woowa.woowakit.domain.order.dto.request.CouponAppliedOrderItemRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderCreateRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderPayRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderSearchRequest;
import com.woowa.woowakit.domain.order.dto.response.OrderDetailResponse;
import com.woowa.woowakit.domain.order.dto.response.OrderItemDetailResponse;
import com.woowa.woowakit.domain.order.dto.response.OrderResponse;
import com.woowa.woowakit.domain.order.dto.response.OrderSimpleResponse;
import com.woowa.woowakit.restDocsHelper.PathParam;
import com.woowa.woowakit.restDocsHelper.RequestFields;
import com.woowa.woowakit.restDocsHelper.ResponseFields;
import com.woowa.woowakit.restDocsHelper.RestDocsTest;

@WebMvcTest(OrderController.class)
@AutoConfigureRestDocs(uriHost = "api.test.com", uriPort = 80)
@ExtendWith(RestDocumentationExtension.class)
class OrderControllerTest extends RestDocsTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderCommandService orderCommandService;

	@MockBean
	private OrderQueryService orderQueryService;

	@Autowired
	private ObjectMapper autowiredObjectMapper;

	@Test
	@DisplayName("[POST] [/orders] 주문 생성 테스트 및 문서화")
	void createPreOrder() throws Exception {
		RequestFields requestFields = new RequestFields(Map.of(
			"[]productId", "주문 상품 ID",
			"[]quantity", "주문 상품 수량"
		));
		ResponseFields responseFields = new ResponseFields(getOrderResponseKeyValues());
		String token = getToken();
		List<OrderCreateRequest> request = List.of(
			OrderCreateRequest.of(1L, 10L),
			OrderCreateRequest.of(2L, 10L)
		);
		Order order = getOrder();
		OrderResponse response = OrderResponse.of(
			order,
			getCouponOrderItems(List.of(getCoupon()), order.getOrderItems().get(0))
		);
		given(orderCommandService.create(any(), any(), any())).willReturn(response);

		mockMvc.perform(post("/orders")
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(handler().methodName("create"))
			.andDo(authorizationDocument("orders/create", requestFields, responseFields));
	}

	@Test
	@DisplayName("[POST] [/orders/{id}/pay] 주문 결제 테스트 및 문서화")
	void createOrder() throws Exception {
		PathParam pathParam = new PathParam("id", "주문 ID");
		RequestFields requestFields = new RequestFields(Map.of(
			"paymentKey", "결제 키",
			"couponAppliedOrderItems[].orderItemId", "주문 상품 ID",
			"couponAppliedOrderItems[].couponId", "주문 상품에 적용할 쿠폰 ID"
		));
		String token = getToken();
		List<CouponAppliedOrderItemRequest> couponAppliedOrderItemRequests = List.of(
			CouponAppliedOrderItemRequest.of(1L, 1L)
		);
		OrderPayRequest request = OrderPayRequest.of("paymentKey", couponAppliedOrderItemRequests);

		mockMvc.perform(post("/orders/{id}/pay", 1L)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(handler().methodName("pay"))
			.andDo(authorizationDocument("orders/pay", pathParam, requestFields));
	}

	@Test
	@DisplayName("[GET] [/orders/{id}] 단일 주문 조회 테스트 및 문서화")
	void getOrderDetail() throws Exception {
		PathParam pathParam = new PathParam("id", "주문 ID");
		ResponseFields responseFields = new ResponseFields(getOrderDetailResponseKeyValues());

		String token = getToken();
		Long orderId = 1L;
		Order order = getOrder();
		OrderDetailResponse response = OrderDetailResponse.of(order, getOrderItemDetailResponses(order));
		given(orderQueryService.findOrderByOrderIdAndMemberId(any(), any())).willReturn(response);

		mockMvc.perform(get("/orders/{id}", orderId)
				.header(HttpHeaders.AUTHORIZATION, token))
			.andExpect(status().isOk())
			.andExpect(handler().methodName("getOrderDetail"))
			.andDo(authorizationDocument("orders/detail", pathParam, responseFields));
	}

	@Test
	@DisplayName("[GET] [/orders] 주문 조회 테스트 및 문서화")
	void getOrderDetails() throws Exception {
		RequestFields requestFields = new RequestFields(Map.of(
			"lastOrderId", "마지막 주문 ID",
			"pageSize", "페이지 사이즈 (기본 20)"
		));
		ResponseFields responseFields = new ResponseFields(Map.of(
			"[]orderId", "주문 아이디",
			"[]uuid", "주문 고유 번호",
			"[]orderStatus", "주문 상태",
			"[]totalPrice", "할인 후 주문 총 가격",
			"[]originTotalPrice", "할인 전 주문 총 가격"
		));

		String token = getToken();
		OrderSearchRequest request = OrderSearchRequest.of(100L, 20);
		List<OrderSimpleResponse> response = OrderSimpleResponse.from(List.of(getOrder()));

		given(orderQueryService.findAllOrderByMemberId(any(), any())).willReturn(response);

		mockMvc.perform(get("/orders")
				.content(autowiredObjectMapper.writeValueAsString(request))
				.header(HttpHeaders.AUTHORIZATION, token))
			.andExpect(status().isOk())
			.andExpect(handler().methodName("getOrderDetail"))
			.andDo(authorizationDocument("orders/details", requestFields, responseFields));
	}

	private HashMap<String, String> getOrderResponseKeyValues() {
		HashMap<String, String> responseKeyValues = new HashMap<>();
		responseKeyValues.put("id", "주문 상품 ID");
		responseKeyValues.put("uuid", "주문 고유 번호");
		responseKeyValues.put("orderItems[].id", "주문 상품 ID");
		responseKeyValues.put("orderItems[].productId", "상품 ID");
		responseKeyValues.put("orderItems[].name", "주문 상품 이름");
		responseKeyValues.put("orderItems[].image", "주문 상품 이미지");
		responseKeyValues.put("orderItems[].price", "주문 상품 가격");
		responseKeyValues.put("orderItems[].quantity", "주문 상품 수량");
		responseKeyValues.put("orderItems[].couponResponses.couponResponses[].couponId", "주문 상품에 적용할 수 있는 쿠폰 ID");
		responseKeyValues.put("orderItems[].couponResponses.couponResponses[].name", "주문 상품에 적용할 수 있는 쿠폰 이름");
		responseKeyValues.put("orderItems[].couponResponses.couponResponses[].expiryDate", "주문 상품에 적용할 수 있는 쿠폰 만료일");
		responseKeyValues.put("orderItems[].couponResponses.couponResponses[].couponType", "주문 상품에 적용할 수 있는 쿠폰 타입");
		responseKeyValues.put(
			"orderItems[].couponResponses.couponResponses[].minimumOrderAmount",
			"주문 상품에 적용할 수 있는 쿠폰의 최소 주문 금액"
		);
		responseKeyValues.put(
			"orderItems[].couponResponses.couponResponses[].discount",
			"주문 상품에 적용할 수 있는 쿠폰의 할인률 혹은 할인 금액"
		);
		responseKeyValues.put(
			"orderItems[].couponResponses.couponResponses[].couponTarget.couponTargetType",
			"주문 상품에 적용할 수 있는 쿠폰 타겟 타입"
		);
		responseKeyValues.put(
			"orderItems[].couponResponses.couponResponses[].couponTarget.productBrand",
			"주문 상품에 적용할 수 있는 쿠폰 타겟 브랜드"
		);
		responseKeyValues.put(
			"orderItems[].couponResponses.couponResponses[].couponTarget.productCategory",
			"주문 상품에 적용할 수 있는 쿠폰 타겟 카테고리"
		);
		responseKeyValues.put(
			"orderItems[].couponResponses.couponResponses[].couponTarget.productId",
			"주문 상품에 적용할 수 있는 쿠폰 타겟 상품 ID"
		);
		return responseKeyValues;
	}

	private HashMap<String, String> getOrderDetailResponseKeyValues() {
		HashMap<String, String> responseKeyValues = new HashMap<>();
		responseKeyValues.put("orderId", "주문 상품 ID");
		responseKeyValues.put("orderStatus", "주문 상태");
		responseKeyValues.put("originTotalPrice", "할인 전 주문 총 가격");
		responseKeyValues.put("totalPrice", "할인 후 주문 총 가격");
		responseKeyValues.put("uuid", "주문 고유 번호");
		responseKeyValues.put("orderItems[].id", "주문 상품 ID");
		responseKeyValues.put("orderItems[].productId", "상품 ID");
		responseKeyValues.put("orderItems[].name", "주문 상품 이름");
		responseKeyValues.put("orderItems[].image", "주문 상품 이미지");
		responseKeyValues.put("orderItems[].price", "주문 상품 가격");
		responseKeyValues.put("orderItems[].quantity", "주문 상품 수량");
		responseKeyValues.put("orderItems[].appliedCoupon", "쿠폰 적용 여부");
		responseKeyValues.put("orderItems[].couponResponse.couponId", "주문 상품에 적용된 쿠폰 ID");
		responseKeyValues.put("orderItems[].couponResponse.name", "주문 상품에 적용된 쿠폰 이름");
		responseKeyValues.put("orderItems[].couponResponse.expiryDate", "주문 상품에 적용된 쿠폰 만료일");
		responseKeyValues.put("orderItems[].couponResponse.couponType", "주문 상품에 적용된 쿠폰 타입");
		responseKeyValues.put("orderItems[].couponResponse.minimumOrderAmount", "주문 상품에 적용된 쿠폰의 최소 주문 금액");
		responseKeyValues.put("orderItems[].couponResponse.discount", "주문 상품에 적용된 쿠폰의 할인률 혹은 할인 금액");
		responseKeyValues.put("orderItems[].couponResponse.couponTarget.couponTargetType", "주문 상품에 적용된 쿠폰 타겟 타입");
		responseKeyValues.put("orderItems[].couponResponse.couponTarget.productBrand", "주문 상품에 적용된 쿠폰 타겟 브랜드");
		responseKeyValues.put("orderItems[].couponResponse.couponTarget.productCategory", "주문 상품에 적용된 쿠폰 타겟 카테고리");
		responseKeyValues.put("orderItems[].couponResponse.couponTarget.productId", "주문 상품에 적용된 쿠폰 타겟 상품 ID");
		return responseKeyValues;
	}

	private List<CouponGroupOrderItem> getCouponOrderItems(
		final List<Coupon> coupons,
		final OrderItem orderItem
	) {
		return List.of(new CouponGroupOrderItem(coupons, orderItem));
	}

	private List<OrderItemDetailResponse> getOrderItemDetailResponses(final Order order) {
		return List.of(
			OrderItemDetailResponse.of(order.getOrderItems().get(0), getCoupon()),
			OrderItemDetailResponse.of(order.getOrderItems().get(1), getCoupon())
		);
	}

	private Coupon getCoupon() {
		return CouponFixture.getAllCouponBuilder()
			.build();
	}

	private Order getOrder() {
		return Order.builder()
			.memberId(1L)
			.orderItems(List.of(
				getOrderItem(1L, "된장 밀키트", 15000L, 20),
				getOrderItem(2L, "닭갈비 밀키트", 30000L, 2)))
			.build();
	}

	private OrderItem getOrderItem(
		final Long productId,
		final String name,
		final long price,
		final int quantity
	) {

		return OrderItem.builder()
			.productId(productId)
			.name(name)
			.image("https://service-hub.org/file/log")
			.price(price)
			.quantity(quantity)
			.build();
	}
}

