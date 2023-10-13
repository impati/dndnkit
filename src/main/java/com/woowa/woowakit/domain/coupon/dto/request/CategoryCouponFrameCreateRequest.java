package com.woowa.woowakit.domain.coupon.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.product.domain.ProductCategory;

import lombok.Getter;

@Getter
public class CategoryCouponFrameCreateRequest extends CouponFrameCreateRequest {

	@NotNull(message = "쿠폰 적용 대상은 필수 값입니다.")
	private ProductCategory category;

	private CategoryCouponFrameCreateRequest(
		final String name,
		final Long durationDay,
		final LocalDate endDate,
		final CouponType couponType,
		final ProductCategory productCategory,
		final int minimumOrderAmount,
		final int discount
	) {
		super(name, durationDay, endDate, couponType, minimumOrderAmount, discount);
		this.category = productCategory;
	}

	public static CategoryCouponFrameCreateRequest of(
		final String name,
		final Long durationDay,
		final LocalDate localDate,
		final CouponType couponType,
		final ProductCategory productCategory,
		final int minimumOrderAmount,
		final int discount
	) {
		return new CategoryCouponFrameCreateRequest(
			name,
			durationDay,
			localDate,
			couponType,
			productCategory,
			minimumOrderAmount,
			discount
		);
	}
}
