package com.woowa.woowakit.domain.coupon.api;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.woowa.woowakit.domain.auth.annotation.Admin;
import com.woowa.woowakit.domain.coupon.application.CouponFrameCommandService;
import com.woowa.woowakit.domain.coupon.application.CouponFrameQueryService;
import com.woowa.woowakit.domain.coupon.domain.CouponFrame;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.dto.request.BrandCouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CategoryCouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.ProductCouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.response.CouponFrameResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CouponController {

	private final CouponFrameCommandService couponFrameCommandService;
	private final CouponFrameQueryService couponFrameQueryService;

	@Admin
	@PostMapping("/coupon-frame/product")
	public ResponseEntity<Void> createProductCouponFrame(
		@Valid @RequestBody final ProductCouponFrameCreateRequest request
	) {
		Long id = couponFrameCommandService.create(CouponTarget.from(request.getProductId()), request);

		return ResponseEntity.created(URI.create("/coupon-frame/" + id)).build();
	}

	@Admin
	@PostMapping("/coupon-frame/brand")
	public ResponseEntity<Void> createBrandCouponFrame(
		@Valid @RequestBody final BrandCouponFrameCreateRequest request
	) {
		Long id = couponFrameCommandService.create(CouponTarget.from(request.getBrand()), request);

		return ResponseEntity.created(URI.create("/coupon-frame/" + id)).build();
	}

	@Admin
	@PostMapping("/coupon-frame/category")
	public ResponseEntity<Void> createCategoryCouponFrame(
		@Valid @RequestBody final CategoryCouponFrameCreateRequest request
	) {
		Long id = couponFrameCommandService.create(CouponTarget.from(request.getCategory()), request);

		return ResponseEntity.created(URI.create("/coupon-frame/" + id)).build();
	}

	@Admin
	@PostMapping("/coupon-frame/all")
	public ResponseEntity<Void> createAllCouponFrame(@Valid @RequestBody final CouponFrameCreateRequest request) {
		Long id = couponFrameCommandService.create(CouponTarget.all(), request);

		return ResponseEntity.created(URI.create("/coupon-frame/" + id)).build();
	}

	@Admin
	@GetMapping("/coupon-frame/{couponFrameId}")
	public ResponseEntity<CouponFrameResponse> findCouponFrame(@PathVariable final Long couponFrameId) {
		CouponFrame couponFrame = couponFrameQueryService.getCouponFrame(couponFrameId);

		return ResponseEntity.ok(CouponFrameResponse.from(couponFrame));
	}
}
