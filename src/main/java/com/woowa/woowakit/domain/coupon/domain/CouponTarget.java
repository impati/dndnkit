package com.woowa.woowakit.domain.coupon.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import com.woowa.woowakit.domain.product.domain.ProductCategory;

import lombok.Getter;

@Getter
@Embeddable
public class CouponTarget {

	@Enumerated(EnumType.STRING)
	@Column(name = "coupon_target_type")
	private CouponTargetType couponTargetType;

	@Column(name = "product_brand")
	@Enumerated(EnumType.STRING)
	private ProductBrand productBrand;

	@Column(name = "product_category")
	@Enumerated(EnumType.STRING)
	private ProductCategory productCategory;

	@Column(name = "product_id")
	private Long productId;

	protected CouponTarget() {
	}

	public static CouponTarget all() {
		return new CouponTarget(CouponTargetType.ALL);
	}

	public static CouponTarget from(final ProductBrand productBrand) {
		return new CouponTarget(CouponTargetType.BRAND, productBrand);
	}

	public static CouponTarget from(final ProductCategory productCategory) {
		return new CouponTarget(CouponTargetType.CATEGORY, productCategory);
	}

	public static CouponTarget from(final Long productId) {
		return new CouponTarget(CouponTargetType.PRODUCT, productId);
	}

	private CouponTarget(final CouponTargetType couponTargetType, final ProductBrand productBrand) {
		this.couponTargetType = couponTargetType;
		this.productBrand = productBrand;
	}

	private CouponTarget(final CouponTargetType couponTargetType, final ProductCategory productCategory) {
		this.couponTargetType = couponTargetType;
		this.productCategory = productCategory;
	}

	private CouponTarget(final CouponTargetType couponTargetType, final Long productId) {
		this.couponTargetType = couponTargetType;
		this.productId = productId;
	}

	private CouponTarget(final CouponTargetType couponTargetType) {
		this.couponTargetType = couponTargetType;
	}

	public boolean isApplicable(final Product product) {
		switch (couponTargetType) {
			case ALL:
				return true;
			case BRAND:
				return this.productBrand == product.getProductBrand();
			case CATEGORY:
				return product.getCategories().contains(this.productCategory);
			case PRODUCT:
				return Objects.equals(this.getProductId(), product.getId());
			default:
				throw new IllegalStateException();
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CouponTarget)) {
			return false;
		}

		final CouponTarget that = (CouponTarget)o;
		return couponTargetType == that.couponTargetType && productBrand == that.productBrand
			&& productCategory == that.productCategory && Objects.equals(productId, that.productId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(couponTargetType, productBrand, productCategory, productId);
	}
}
