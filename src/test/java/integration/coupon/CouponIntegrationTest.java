package integration.coupon;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.dto.request.CouponCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.response.CouponResponse;
import com.woowa.woowakit.domain.coupon.dto.response.CouponResponses;

import integration.IntegrationTest;
import integration.helper.CommonRestAssuredUtils;
import integration.helper.CouponHelper;
import integration.helper.MemberHelper;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("쿠폰 도메인 인수 테스트")
class CouponIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("사용자에게 쿠폰틀로 쿠폰을 발급한다.")
	void createCouponByCouponFrame() {
		// given
		String adminAccessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
		Long couponFrameId = CouponHelper.createAllCouponFrame(adminAccessToken);
		String accessToken = MemberHelper.signUpAndLogIn();
		CouponCreateRequest request = CouponHelper.createCouponCreateRequest(couponFrameId);

		// when
		ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
			"/coupons",
			request,
			accessToken
		);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).matches("^/coupons/[0-9]+$");
	}

	@Test
	@DisplayName("발급 받은 쿠폰을 사용자가 조회할 수 있어야한다.")
	void findCouponByMember() {
		// given
		String adminAccessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
		Long couponFrameId = CouponHelper.createAllCouponFrame(adminAccessToken);
		String accessToken = MemberHelper.signUpAndLogIn();
		CouponHelper.createCouponOfMember(couponFrameId, accessToken);

		// when
		ExtractableResponse<Response> response = CommonRestAssuredUtils.get(
			"/coupons",
			accessToken
		);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		CouponResponses couponResponses = response.as(CouponResponses.class);
		assertThat(couponResponses.getCouponResponses())
			.hasSize(1)
			.extracting(
				CouponResponse::getName,
				CouponResponse::getExpiryDate,
				CouponResponse::getCouponType,
				CouponResponse::getMinimumOrderAmount,
				CouponResponse::getDiscount,
				CouponResponse::getCouponTarget)
			.contains(
				tuple("default",
					LocalDate.now().plusDays(7),
					CouponType.FIXED,
					17000,
					1000,
					CouponTarget.all()));
	}
}
