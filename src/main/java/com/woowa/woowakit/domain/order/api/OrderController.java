package com.woowa.woowakit.domain.order.api;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woowa.woowakit.domain.auth.annotation.Authenticated;
import com.woowa.woowakit.domain.auth.annotation.User;
import com.woowa.woowakit.domain.auth.domain.AuthPrincipal;
import com.woowa.woowakit.domain.order.application.OrderCommandService;
import com.woowa.woowakit.domain.order.application.OrderQueryService;
import com.woowa.woowakit.domain.order.dto.request.OrderCreateRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderPayRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderSearchRequest;
import com.woowa.woowakit.domain.order.dto.response.OrderDetailResponse;
import com.woowa.woowakit.domain.order.dto.response.OrderResponse;
import com.woowa.woowakit.domain.order.dto.response.OrderSimpleResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderCommandService orderCommandService;
	private final OrderQueryService orderQueryService;

	@User
	@PostMapping
	public ResponseEntity<OrderResponse> create(
		@Authenticated final AuthPrincipal authPrincipal,
		@Valid @RequestBody final List<OrderCreateRequest> request
	) {
		final OrderResponse response = orderCommandService.create(authPrincipal, request, LocalDate.now());

		return ResponseEntity.created(URI.create("/orders/" + response.getId())).body(response);
	}

	@User
	@PostMapping("/{orderId}/pay")
	public ResponseEntity<Void> pay(
		@Authenticated final AuthPrincipal authPrincipal,
		@PathVariable final Long orderId,
		@Valid @RequestBody final OrderPayRequest request
	) {
		orderCommandService.pay(authPrincipal, orderId, request);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@User
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDetailResponse> getOrderDetail(
		@Authenticated final AuthPrincipal authPrincipal,
		@PathVariable final Long orderId
	) {
		return ResponseEntity.ok(orderQueryService.findOrderByOrderIdAndMemberId(authPrincipal, orderId));
	}

	@User
	@GetMapping
	public ResponseEntity<List<OrderSimpleResponse>> getOrderDetail(
		@Authenticated final AuthPrincipal authPrincipal,
		@ModelAttribute @Valid final OrderSearchRequest request
	) {
		return ResponseEntity.ok(orderQueryService.findAllOrderByMemberId(authPrincipal, request));
	}
}
