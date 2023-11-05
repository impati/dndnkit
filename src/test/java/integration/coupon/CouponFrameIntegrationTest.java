package integration.coupon;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.dto.request.BrandCouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CategoryCouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.ProductCouponFrameCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.response.CouponFrameResponse;
import com.woowa.woowakit.domain.coupon.dto.response.CouponFrameResponses;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import com.woowa.woowakit.domain.product.domain.ProductStatus;

import integration.IntegrationTest;
import integration.helper.CommonRestAssuredUtils;
import integration.helper.CouponHelper;
import integration.helper.MemberHelper;
import integration.helper.ProductHelper;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("쿠폰틀 도메인 인수 테스트")
class CouponFrameIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("쿠폰을 생성할 정보와 적용할 상품 ID를 입력 받아 쿠폰틀 도메인을 생성한다.")
	void createCouponFrameCouponTargetIsProductId() {
		// given
		String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
		Long productId = ProductHelper.createProductAndSetUp(100, ProductStatus.IN_STOCK);
		ProductCouponFrameCreateRequest request = CouponHelper.createProductCouponFrameCreateRequest(
			CouponType.FIXED,
			productId,
			1000
		);

		// when
		ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
			"/coupon-frames/product",
			request,
			accessToken
		);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).matches("^/coupon-frames/[0-9]+$");
	}

	@Test
	@DisplayName("쿠폰을 생성할 정보와 적용할 브랜드를 입력 받아 쿠폰틀 도메인을 생성한다.")
	void createCouponFrameCouponTargetIsBrand() {
		// given
		String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
		ProductBrand productBrand = ProductBrand.MOKRAN;
		BrandCouponFrameCreateRequest request = CouponHelper.createBrandCouponFrameCreateRequest(
			CouponType.FIXED,
			productBrand,
			1000
		);

		// when
		ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
			"/coupon-frames/brand",
			request,
			accessToken
		);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).matches("^/coupon-frames/[0-9]+$");
	}

	@Test
	@DisplayName("쿠폰을 생성할 정보와 적용할 카테고리를 입력 받아 쿠폰틀 도메인을 생성한다.")
	void createCouponFrameCouponTargetIsCategory() {
		// given
		String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
		ProductCategory category = ProductCategory.CHINESE;
		CategoryCouponFrameCreateRequest request = CouponHelper.createCategoryCouponFrameCreateRequest(
			CouponType.FIXED,
			category,
			1000
		);

		// when
		ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
			"/coupon-frames/category",
			request,
			accessToken
		);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).matches("^/coupon-frames/[0-9]+$");
	}

	@Test
	@DisplayName("쿠폰을 생성할 정보를 입력받아 어디에도 적용할 수 있는 쿠폰틀 도메인을 생성한다.")
	void createCouponFrameCouponTargetIsAll() {
		// given
		String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
		CouponFrameCreateRequest request = CouponHelper.createAllCouponFrameCreateRequest();

		// when
		ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
			"/coupon-frames/all",
			request,
			accessToken
		);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).matches("^/coupon-frames/[0-9]+$");
	}

	@Test
	@DisplayName("쿠폰틀 ID 로 쿠폰틀 정보를 조회한다.")
	void findCouponFrame() {
		// given
		String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
		Long couponFrameId = CouponHelper.createAllCouponFrame(accessToken);

		// when
		ExtractableResponse<Response> response = CommonRestAssuredUtils.get(
			"/coupon-frames/" + couponFrameId,
			accessToken
		);

		// then
		CouponFrameResponse detailResponse = response.as(CouponFrameResponse.class);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(detailResponse)
			.extracting(CouponFrameResponse::getName, CouponFrameResponse::getCouponTarget)
			.contains("default", CouponTarget.all());
	}

	@Test
	@DisplayName("유효한 쿠폰틀들을 정보를 조회한다.")
	void findCouponFrames() {
		// given
		String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
		CouponHelper.createAllCouponFrame(accessToken);
		CouponHelper.creatBrandCouponFrame(accessToken);

		// when
		ExtractableResponse<Response> response = CommonRestAssuredUtils.get(
			"/coupon-frames",
			accessToken
		);

		// then
		CouponFrameResponses detailResponses = response.as(CouponFrameResponses.class);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(detailResponses.getCouponFrameResponses()).hasSize(2);
	}
}
