package com.woowa.woowakit.domain.coupon.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.woowa.woowakit.domain.fixture.ProductFixture;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import com.woowa.woowakit.domain.product.domain.ProductCategory;

class CouponTest {

	@Test
	@DisplayName("한식 카테고리 쿠폰은 한식 상품에 적용할 수 있다.")
	void isApplicableTrue() {
		Product product = getProduct(ProductCategory.KOREAN);
		RateCoupon rateCoupon = getRateCoupon(ProductCategory.KOREAN, "한식 카테고리 밀키트 10% 할인 쿠폰");

		assertThat(rateCoupon.isApplicable(product)).isTrue();
	}

	@Test
	@DisplayName("한식 카테고리 쿠폰은 중식 상품에 적용할 수 없다.")
	void isApplicableFalse() {
		Product product = getProduct(ProductCategory.CHINESE);
		RateCoupon rateCoupon = getRateCoupon(ProductCategory.KOREAN, "한식 카테고리 밀키트 10% 할인 쿠폰");

		assertThat(rateCoupon.isApplicable(product)).isFalse();
	}

	@Test
	@DisplayName("브랜드 종류가 같다면 쿠폰을 적용할 수 있다.")
	void isApplicableBrandTrue() {
		Product product = getProduct(ProductBrand.COOKIT);
		RateCoupon rateCoupon = getRateCoupon(ProductBrand.COOKIT);

		assertThat(rateCoupon.isApplicable(product)).isTrue();
	}

	@Test
	@DisplayName("브랜드 종류가 다르다면 쿠폰을 적용할 수 없다.")
	void isApplicableBrandFalse() {
		Product product = getProduct(ProductBrand.FRESH_EASY);
		RateCoupon rateCoupon = getRateCoupon(ProductBrand.COOKIT);

		assertThat(rateCoupon.isApplicable(product)).isFalse();
	}

	@Test
	@DisplayName("상품 ID 가 같다면 쿠폰을 적용할 수 있다.")
	void isApplicableProductIdTrue() {
		Product product = getProduct(1L);
		RateCoupon rateCoupon = getRateCoupon(1L);

		assertThat(rateCoupon.isApplicable(product)).isTrue();
	}

	@Test
	@DisplayName("상품 ID 가 다르다면 쿠폰을 적용할 수 없다")
	void isApplicableProductIdFalse() {
		Product product = getProduct(2L);
		RateCoupon rateCoupon = getRateCoupon(1L);

		assertThat(rateCoupon.isApplicable(product)).isFalse();
	}

	@ParameterizedTest
	@MethodSource
	@DisplayName("모든 상품에 대해 적용할 수 있는 쿠폰이라면 모두 적용할 수 있다.")
	void isApplicableAll(final Product product) {
		RateCoupon rateCoupon = getAllRateCoupon();

		assertThat(rateCoupon.isApplicable(product)).isTrue();
	}

	private static Stream<Arguments> isApplicableAll() {
		return Stream.of(
			arguments(getProduct(1L)),
			arguments(getProduct(2L)),
			arguments(getProduct(ProductBrand.FRESH_EASY)),
			arguments(getProduct(ProductBrand.COOKIT)),
			arguments(getProduct(ProductBrand.MOKRAN)),
			arguments(getProduct(ProductBrand.MYCHEF)),
			arguments(getProduct(ProductBrand.NONE)),
			arguments(getProduct(ProductCategory.KOREAN)),
			arguments(getProduct(ProductCategory.CHINESE)),
			arguments(getProduct(ProductCategory.ETC)),
			arguments(getProduct(ProductCategory.JAPANESE)),
			arguments(getProduct(ProductCategory.WESTERN)));
	}

	private RateCoupon getAllRateCoupon() {
		return getRateCouponBuilder()
			.couponTarget(CouponTarget.all())
			.build();
	}

	private RateCoupon getRateCoupon(final ProductBrand brand) {
		return getRateCouponBuilder()
			.couponTarget(CouponTarget.from(brand))
			.build();
	}

	private static Product getProduct(final Long productId) {
		return ProductFixture.getInStockProductBuilder()
			.id(productId)
			.build();
	}

	private static Product getProduct(final ProductCategory productCategory) {
		return ProductFixture.getInStockProductBuilder()
			.productCategories(List.of(productCategory))
			.build();
	}

	private static Product getProduct(final ProductBrand productBrand) {
		return ProductFixture.getInStockProductBuilder()
			.productBrand(productBrand)
			.build();
	}

	private RateCoupon getRateCoupon(final long productId) {
		return getRateCouponBuilder()
			.couponTarget(CouponTarget.from(productId))
			.build();
	}

	private RateCoupon getRateCoupon(final ProductCategory productCategory, final String name) {
		return getRateCouponBuilder()
			.couponTarget(CouponTarget.from(productCategory))
			.name(name)
			.build();
	}

	private RateCoupon.RateCouponBuilder getRateCouponBuilder() {
		return RateCoupon.builder()
			.discountRate(10)
			.couponTarget(CouponTarget.from(ProductCategory.KOREAN))
			.expiryDate(LocalDate.of(2023, 12, 31))
			.minimumOrderAmount(17000)
			.memberId(1L)
			.name("한식 카테고리 밀키트 10% 할인 쿠폰");
	}
}
