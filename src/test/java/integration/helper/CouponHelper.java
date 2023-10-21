package integration.helper;

import java.time.LocalDate;

import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.dto.request.BrandCouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CategoryCouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CouponCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.ProductCouponFrameCreateRequest;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import com.woowa.woowakit.domain.product.domain.ProductCategory;

public class CouponHelper {

	public static ProductCouponFrameCreateRequest createProductCouponFrameCreateRequest(
		final CouponType couponType,
		final Long productId,
		final int discount

	) {
		return ProductCouponFrameCreateRequest.of(
			"default",
			7L,
			LocalDate.of(2023, 12, 31),
			couponType,
			productId,
			17000,
			discount
		);
	}

	public static BrandCouponFrameCreateRequest createBrandCouponFrameCreateRequest(
		final CouponType couponType,
		final ProductBrand productBrand,
		final int discount

	) {
		return BrandCouponFrameCreateRequest.of(
			"default",
			7L,
			LocalDate.of(2023, 12, 31),
			couponType,
			productBrand,
			17000,
			discount
		);
	}

	public static CategoryCouponFrameCreateRequest createCategoryCouponFrameCreateRequest(
		final CouponType couponType,
		final ProductCategory productCategory,
		final int discount

	) {
		return CategoryCouponFrameCreateRequest.of(
			"default",
			7L,
			LocalDate.of(2023, 12, 31),
			couponType,
			productCategory,
			17000,
			discount
		);
	}

	public static CouponFrameCreateRequest createAllCouponFrameCreateRequest() {
		return new CouponFrameCreateRequest(
			"default",
			7L,
			LocalDate.of(2023, 12, 31),
			CouponType.FIXED,
			17000,
			1000
		);
	}

	public static CouponCreateRequest createCouponCreateRequest(final Long couponFrameId) {
		return CouponCreateRequest.from(couponFrameId);
	}

	public static Long createAllCouponFrame(final String accessToken) {
		return getIdFrom(CommonRestAssuredUtils.post(
			"/coupon-frames/all",
			createAllCouponFrameCreateRequest(),
			accessToken
		).header("Location"));
	}

	public static Long creatBrandCouponFrame(final String accessToken) {
		return getIdFrom(CommonRestAssuredUtils.post(
			"/coupon-frames/brand",
			createBrandCouponFrameCreateRequest(CouponType.RATED, ProductBrand.MOKRAN, 10),
			accessToken
		).header("Location"));
	}

	public static void createCouponOfMember(final Long couponFrameId, final String accessToken) {
		CouponCreateRequest request = CouponHelper.createCouponCreateRequest(couponFrameId);
		CommonRestAssuredUtils.post(
			"/coupons",
			request,
			accessToken
		);
	}

	private static Long getIdFrom(String location) {
		String[] parts = location.split("/");
		return Long.parseLong(parts[parts.length - 1]);
	}
}
