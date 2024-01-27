package integration.helper;

import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponDeployType;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.domain.IssueType;
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
                LocalDate.of(3023, 12, 31),
                couponType,
                productId,
                17000,
                discount,
                CouponDeployType.NO_LIMIT,
                null,
                IssueType.REPEATABLE
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
                LocalDate.of(3023, 12, 31),
                couponType,
                productBrand,
                17000,
                discount,
                CouponDeployType.NO_LIMIT,
                null,
                IssueType.REPEATABLE
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
                LocalDate.of(3023, 12, 31),
                couponType,
                productCategory,
                17000,
                discount,
                CouponDeployType.NO_LIMIT,
                null,
                IssueType.REPEATABLE
        );
    }

    public static CouponGroupCreateRequest createAllCouponGroupCreateRequest() {
        return new CouponGroupCreateRequest(
                "default",
                7L,
                LocalDate.of(3023, 12, 31),
                CouponType.FIXED,
                17000,
                1000,
                CouponDeployType.NO_LIMIT,
                null,
                IssueType.REPEATABLE
        );
    }

    public static CouponGroupCreateRequest createAllCouponGroupCreateRequest(final CouponDeploy couponDeploy) {
        return new CouponGroupCreateRequest(
                "default",
                7L,
                LocalDate.of(3023, 12, 31),
                CouponType.FIXED,
                17000,
                1000,
                couponDeploy.getCouponDeployType(),
                couponDeploy.getDeployAmount(),
                IssueType.REPEATABLE
        );
    }

    public static CouponCreateRequest createCouponCreateRequest(final Long couponGroupId) {
        return CouponCreateRequest.from(couponGroupId);
    }

    public static Long createAllCouponGroup(final String accessToken) {
        Long couponGroupId = getIdFrom(CommonRestAssuredUtils.post(
                "/coupon-groups/all",
                createAllCouponGroupCreateRequest(),
                accessToken
        ).header("Location"));
        callDeploy(couponGroupId, accessToken);
        return couponGroupId;
    }

    public static Long createAllCouponGroup(final String accessToken, final CouponDeploy couponDeploy) {
        Long couponGroupId = getIdFrom(CommonRestAssuredUtils.post(
                "/coupon-groups/all",
                createAllCouponGroupCreateRequest(couponDeploy),
                accessToken
        ).header("Location"));
        callDeploy(couponGroupId, accessToken);
        return couponGroupId;
    }

    public static Long creatBrandCouponGroup(final String accessToken) {
        Long couponGroupId = getIdFrom(CommonRestAssuredUtils.post(
                "/coupon-groups/brand",
                createBrandCouponGroupCreateRequest(CouponType.RATED, ProductBrand.MOKRAN, 10),
                accessToken
        ).header("Location"));
        callDeploy(couponGroupId, accessToken);
        return couponGroupId;
    }

    public static Long createCouponOfMember(final Long couponGroupId, final String accessToken) {
        CouponCreateRequest request = CouponHelper.createCouponCreateRequest(couponGroupId);
        return getIdFrom(CommonRestAssuredUtils.post(
                "/coupons",
                request,
                accessToken
        ).header("Location"));
    }

    private static void callDeploy(Long couponGroupId, String accessToken) {
        CommonRestAssuredUtils.post(
                "/coupon-groups/{couponId}/deploy", couponGroupId,
                accessToken
        );
    }

    private static Long getIdFrom(String location) {
        String[] parts = location.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
