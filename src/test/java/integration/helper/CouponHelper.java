package integration.helper;

import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.dto.request.BrandCouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CategoryCouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CouponCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.ProductCouponGroupCreateRequest;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.LocalDate;

public class CouponHelper {

    public static ProductCouponGroupCreateRequest createProductCouponGroupCreateRequest(
            final CouponType couponType,
            final Long productId,
            final int discount

    ) {
        return ProductCouponGroupCreateRequest.of(
                "default",
                7L,
                LocalDate.of(2023, 12, 31),
                couponType,
                productId,
                17000,
                discount
        );
    }

    public static BrandCouponGroupCreateRequest createBrandCouponGroupCreateRequest(
            final CouponType couponType,
            final ProductBrand productBrand,
            final int discount

    ) {
        return BrandCouponGroupCreateRequest.of(
                "default",
                7L,
                LocalDate.of(2023, 12, 31),
                couponType,
                productBrand,
                17000,
                discount
        );
    }

    public static CategoryCouponGroupCreateRequest createCategoryCouponGroupCreateRequest(
            final CouponType couponType,
            final ProductCategory productCategory,
            final int discount

    ) {
        return CategoryCouponGroupCreateRequest.of(
                "default",
                7L,
                LocalDate.of(2023, 12, 31),
                couponType,
                productCategory,
                17000,
                discount
        );
    }

    public static CouponGroupCreateRequest createAllCouponGroupCreateRequest() {
        return new CouponGroupCreateRequest(
                "default",
                7L,
                LocalDate.of(2023, 12, 31),
                CouponType.FIXED,
                17000,
                1000
        );
    }

    public static CouponCreateRequest createCouponCreateRequest(final Long couponGroupId) {
        return CouponCreateRequest.from(couponGroupId);
    }

    public static Long createAllCouponGroup(final String accessToken) {
        return getIdFrom(CommonRestAssuredUtils.post(
                "/coupon-frames/all",
                createAllCouponGroupCreateRequest(),
                accessToken
        ).header("Location"));
    }

    public static Long creatBrandCouponGroup(final String accessToken) {
        return getIdFrom(CommonRestAssuredUtils.post(
                "/coupon-frames/brand",
                createBrandCouponGroupCreateRequest(CouponType.RATED, ProductBrand.MOKRAN, 10),
                accessToken
        ).header("Location"));
    }

    public static Long createCouponOfMember(final Long couponGroupId, final String accessToken) {
        CouponCreateRequest request = CouponHelper.createCouponCreateRequest(couponGroupId);
        return getIdFrom(CommonRestAssuredUtils.post(
                "/coupons",
                request,
                accessToken
        ).header("Location"));
    }

    private static Long getIdFrom(String location) {
        String[] parts = location.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
