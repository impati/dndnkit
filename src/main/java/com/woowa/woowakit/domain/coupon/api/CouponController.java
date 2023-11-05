package com.woowa.woowakit.domain.coupon.api;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woowa.woowakit.domain.auth.annotation.Authenticated;
import com.woowa.woowakit.domain.auth.annotation.User;
import com.woowa.woowakit.domain.auth.domain.AuthPrincipal;
import com.woowa.woowakit.domain.coupon.application.CouponCommandService;
import com.woowa.woowakit.domain.coupon.application.CouponQueryService;
import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.dto.request.CouponCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.response.CouponResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

	private final CouponCommandService couponCommandService;
	private final CouponQueryService couponQueryService;

	@User
	@PostMapping
	public ResponseEntity<Void> createCoupon(
		@Authenticated final AuthPrincipal authPrincipal,
		@Valid @RequestBody final CouponCreateRequest request
	) {
		Long id = couponCommandService.create(authPrincipal.getId(), request.getCouponFrameId(), LocalDate.now());

		return ResponseEntity.created(URI.create("/coupons/" + id)).build();
	}

	@User
	@GetMapping
	public ResponseEntity<CouponResponses> findCouponOfMember(@Authenticated final AuthPrincipal authPrincipal) {
		List<Coupon> coupons = couponQueryService.getCouponsByMember(authPrincipal.getId(), LocalDate.now());

		return ResponseEntity.ok(CouponResponses.from(coupons));
	}
}
