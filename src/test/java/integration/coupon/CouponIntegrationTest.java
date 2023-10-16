package integration.coupon;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.woowa.woowakit.domain.coupon.dto.request.CouponCreateRequest;

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
}
