package integration.coupon;

import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.dto.request.BrandCouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CategoryCouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.ProductCouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.response.CouponGroupResponse;
import com.woowa.woowakit.domain.coupon.dto.response.CouponGroupResponses;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("쿠폰틀 도메인 인수 테스트")
class CouponGroupIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("쿠폰을 생성할 정보와 적용할 상품 ID를 입력 받아 쿠폰그룹 도메인을 생성한다.")
    void createCouponGroupCouponTargetIsProductId() {
        // given
        String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
        Long productId = ProductHelper.createProductAndSetUp(100, ProductStatus.IN_STOCK);
        ProductCouponGroupCreateRequest request = CouponHelper.createProductCouponGroupCreateRequest(
                CouponType.FIXED,
                productId,
                1000
        );

        // when
        ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
                "/coupon-groups/product",
                request,
                accessToken
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).matches("^/coupon-groups/[0-9]+$");
    }

    @Test
    @DisplayName("쿠폰을 생성할 정보와 적용할 브랜드를 입력 받아 쿠폰그룹 도메인을 생성한다.")
    void createCouponGroupCouponTargetIsBrand() {
        // given
        String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
        ProductBrand productBrand = ProductBrand.MOKRAN;
        BrandCouponGroupCreateRequest request = CouponHelper.createBrandCouponGroupCreateRequest(
                CouponType.FIXED,
                productBrand,
                1000
        );

        // when
        ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
                "/coupon-groups/brand",
                request,
                accessToken
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).matches("^/coupon-groups/[0-9]+$");
    }

    @Test
    @DisplayName("쿠폰을 생성할 정보와 적용할 카테고리를 입력 받아 쿠폰그룹 도메인을 생성한다.")
    void createCouponGroupCouponTargetIsCategory() {
        // given
        String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
        ProductCategory category = ProductCategory.CHINESE;
        CategoryCouponGroupCreateRequest request = CouponHelper.createCategoryCouponGroupCreateRequest(
                CouponType.FIXED,
                category,
                1000
        );

        // when
        ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
                "/coupon-groups/category",
                request,
                accessToken
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).matches("^/coupon-groups/[0-9]+$");
    }

    @Test
    @DisplayName("쿠폰을 생성할 정보를 입력받아 어디에도 적용할 수 있는 쿠폰그룹 도메인을 생성한다.")
    void createCouponGroupCouponTargetIsAll() {
        // given
        String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
        CouponGroupCreateRequest request = CouponHelper.createAllCouponGroupCreateRequest();

        // when
        ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
                "/coupon-groups/all",
                request,
                accessToken
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).matches("^/coupon-groups/[0-9]+$");
    }

    @Test
    @DisplayName("쿠폰그룹 ID 로 쿠폰틀 정보를 조회한다.")
    void findCouponGroup() {
        // given
        String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
        Long couponGroupId = CouponHelper.createAllCouponGroup(accessToken);

        // when
        ExtractableResponse<Response> response = CommonRestAssuredUtils.get(
                "/coupon-groups/" + couponGroupId,
                accessToken
        );

        // then
        CouponGroupResponse detailResponse = response.as(CouponGroupResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(detailResponse)
                .extracting(CouponGroupResponse::getName, CouponGroupResponse::getCouponTarget)
                .contains("default", CouponTarget.all());
    }

    @Test
    @DisplayName("유효한 쿠폰그룹 들을 정보를 조회한다.")
    void findCouponGroups() {
        // given
        String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
        CouponHelper.createAllCouponGroup(accessToken);
        CouponHelper.creatBrandCouponGroup(accessToken);

        // when
        ExtractableResponse<Response> response = CommonRestAssuredUtils.get(
                "/coupon-groups",
                accessToken
        );

        // then
        CouponGroupResponses detailResponses = response.as(CouponGroupResponses.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(detailResponses.getCouponGroupResponses()).hasSize(2);
    }

    @Test
    @DisplayName("쿠폰 배포 수량 만큼 쿠폰을 배포한다.")
    void CouponDeploy() {
        // given
        String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
        Long couponGroupId = CouponHelper.createAllCouponGroup(accessToken, CouponDeploy.getDeployLimitInstance(1000));

        // when
        ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
                "/coupon-groups/{couponId}/deploy", couponGroupId,
                accessToken
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("쿠폰 그룹을 배포하면 쿠폰그룹은 배포 상태가 되며 배포 상태에서 발급이 가능하다.")
    void noLimitCouponDeploy() {
        // given
        String accessToken = MemberHelper.login(MemberHelper.createAdminLoginRequest());
        Long couponGroupId = CouponHelper.createAllCouponGroup(accessToken, CouponDeploy.getDeployNoLimitInstance());

        // when
        ExtractableResponse<Response> response = CommonRestAssuredUtils.post(
                "/coupon-groups/{couponId}/deploy", couponGroupId,
                accessToken
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
