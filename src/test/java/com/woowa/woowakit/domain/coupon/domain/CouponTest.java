package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.fixture.ProductFixture;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CouponTest {

    @Test
    @DisplayName("한식 카테고리 쿠폰은 한식 상품에 적용할 수 있다.")
    void isApplicableTrue() {
        Product product = getProduct(ProductCategory.KOREAN);
        Coupon rateCoupon = getRateCoupon(ProductCategory.KOREAN, "한식 카테고리 밀키트 10% 할인 쿠폰");

        assertThat(rateCoupon.isApplicable(product)).isTrue();
    }

    @Test
    @DisplayName("한식 카테고리 쿠폰은 중식 상품에 적용할 수 없다.")
    void isApplicableFalse() {
        Product product = getProduct(ProductCategory.CHINESE);
        Coupon rateCoupon = getRateCoupon(ProductCategory.KOREAN, "한식 카테고리 밀키트 10% 할인 쿠폰");

        assertThat(rateCoupon.isApplicable(product)).isFalse();
    }

    @Test
    @DisplayName("브랜드 종류가 같다면 쿠폰을 적용할 수 있다.")
    void isApplicableBrandTrue() {
        Product product = getProduct(ProductBrand.COOKIT);
        Coupon rateCoupon = getRateCoupon(ProductBrand.COOKIT);

        assertThat(rateCoupon.isApplicable(product)).isTrue();
    }

    @Test
    @DisplayName("브랜드 종류가 다르다면 쿠폰을 적용할 수 없다.")
    void isApplicableBrandFalse() {
        Product product = getProduct(ProductBrand.FRESH_EASY);
        Coupon rateCoupon = getRateCoupon(ProductBrand.COOKIT);

        assertThat(rateCoupon.isApplicable(product)).isFalse();
    }

    @Test
    @DisplayName("상품 ID 가 같다면 쿠폰을 적용할 수 있다.")
    void isApplicableProductIdTrue() {
        Product product = getProduct(1L);
        Coupon rateCoupon = getRateCoupon(1L);

        assertThat(rateCoupon.isApplicable(product)).isTrue();
    }

    @Test
    @DisplayName("상품 ID 가 다르다면 쿠폰을 적용할 수 없다")
    void isApplicableProductIdFalse() {
        Product product = getProduct(2L);
        Coupon rateCoupon = getRateCoupon(1L);

        assertThat(rateCoupon.isApplicable(product)).isFalse();
    }

    @Test
    @DisplayName("쿠폰의 memberId와 입력으로오는 memberId와 같으면 쿠폰 주인이다. ")
    void isOwnerTrue() {
        Long memberId = 1L;
        Coupon coupon = getCoupon(memberId);

        assertThat(coupon.isOwner(memberId)).isTrue();
    }

    @Test
    @DisplayName("쿠폰 타입이 FIXED 인 경우 할인 금액은 discount 값과 동일하다.")
    void computeDiscountPriceFixed() {
        Coupon fixedCoupon = getFixedCoupon(CouponTarget.all(), 5000);

        int discountPrice = fixedCoupon.computeDiscountPrice(17000);

        assertThat(discountPrice).isEqualTo(5000);
    }

    @Test
    @DisplayName("쿠폰 타입이 FIXED 인 경우 할인 금액은 discount 값과 동일하다.")
    void computeDiscountPriceRated() {
        Coupon rateCoupon = getRateCoupon(CouponTarget.all(), 40);

        int discountPrice = rateCoupon.computeDiscountPrice(17000);

        assertThat(discountPrice).isEqualTo(1700 * 4);
    }

    @Test
    @DisplayName("쿠폰을 사용한다면 enable 값이 false 여야한다.")
    void usedCoupon() {
        Coupon rateCoupon = getRateCoupon(CouponTarget.all(), 40);

        rateCoupon.used();

        assertThat(rateCoupon.isEnabled()).isFalse();
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("모든 상품에 대해 적용할 수 있는 쿠폰이라면 모두 적용할 수 있다.")
    void isApplicableAll(final Product product) {
        Coupon rateCoupon = getAllRateCoupon();

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

    private Coupon getAllRateCoupon() {
        return getRateCouponBuilder()
                .couponTarget(CouponTarget.all())
                .build();
    }

    private Coupon getRateCoupon(final ProductBrand brand) {
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

    private Coupon getFixedCoupon(final CouponTarget couponTarget, final int discount) {
        return getRateCouponBuilder()
                .couponTarget(couponTarget)
                .couponType(CouponType.FIXED)
                .discount(discount)
                .build();
    }

    private Coupon getRateCoupon(final long productId) {
        return getRateCouponBuilder()
                .couponTarget(CouponTarget.from(productId))
                .build();
    }

    private Coupon getRateCoupon(final CouponTarget couponTarget, final int discount) {
        return getRateCouponBuilder()
                .couponTarget(couponTarget)
                .couponType(CouponType.RATED)
                .discount(discount)
                .build();
    }

    private Coupon getRateCoupon(final ProductCategory productCategory, final String name) {
        return getRateCouponBuilder()
                .couponTarget(CouponTarget.from(productCategory))
                .name(name)
                .build();
    }

    private Coupon getCoupon(final Long memberId) {
        return getRateCouponBuilder()
                .memberId(memberId)
                .build();
    }

    private Coupon.CouponBuilder getRateCouponBuilder() {
        return Coupon.builder()
                .discount(10)
                .couponType(CouponType.RATED)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .expiryDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .memberId(1L)
                .name("한식 카테고리 밀키트 10% 할인 쿠폰");
    }
}
