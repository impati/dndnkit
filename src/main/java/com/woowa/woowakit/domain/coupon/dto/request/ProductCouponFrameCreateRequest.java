package com.woowa.woowakit.domain.coupon.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.woowa.woowakit.domain.coupon.domain.CouponType;

import lombok.Getter;

@Getter
public class ProductCouponFrameCreateRequest extends CouponFrameCreateRequest {

	@NotNull(message = "쿠폰 적용 대상은 필수 값입니다.")
	private Long productId;

	private ProductCouponFrameCreateRequest(
		final String name,
		final Long durationDay,
		final LocalDate endDate,
		final CouponType couponType,
		final Long productId,
		final int minimumOrderAmount,
		final int discount
	) {
		super(name, durationDay, endDate, couponType, minimumOrderAmount, discount);
		this.productId = productId;
	}

	public static ProductCouponFrameCreateRequest of(
		final String name,
		final Long durationDay,
		final LocalDate localDate,
		final CouponType couponType,
		final Long productId,
		final int minimumOrderAmount,
		final int discount
	) {
		return new ProductCouponFrameCreateRequest(
			name,
			durationDay,
			localDate,
			couponType,
			productId,
			minimumOrderAmount,
			discount
		);
	}
}
