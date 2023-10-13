package com.woowa.woowakit.domain.coupon.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.product.domain.ProductBrand;

import lombok.Getter;

@Getter
public class BrandCouponFrameCreateRequest extends CouponFrameCreateRequest {

	@NotNull(message = "쿠폰 적용 대상은 필수 값입니다.")
	private ProductBrand brand;

	private BrandCouponFrameCreateRequest(
		final String name,
		final Long durationDay,
		final LocalDate endDate,
		final CouponType couponType,
		final ProductBrand productBrand,
		final int minimumOrderAmount,
		final int discount
	) {
		super(name, durationDay, endDate, couponType, minimumOrderAmount, discount);
		this.brand = productBrand;
	}

	public static BrandCouponFrameCreateRequest of(
		final String name,
		final Long durationDay,
		final LocalDate localDate,
		final CouponType couponType,
		final ProductBrand productBrand,
		final int minimumOrderAmount,
		final int discount
	) {
		return new BrandCouponFrameCreateRequest(
			name,
			durationDay,
			localDate,
			couponType,
			productBrand,
			minimumOrderAmount,
			discount
		);
	}
}
