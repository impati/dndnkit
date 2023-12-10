package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.CouponGroupExpiredException;
import com.woowa.woowakit.domain.coupon.exception.IssueCouponException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CouponGroupTest {

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰그룹'을 생성한다.")
    void createCouponGroup() {
        CouponGroup couponGroup = getDefaultCouponGroup();

        assertThat(couponGroup)
                .extracting(
                        CouponGroup::getName,
                        CouponGroup::getDuration,
                        CouponGroup::getEndDate,
                        CouponGroup::getMinimumOrderAmount,
                        CouponGroup::getCouponTarget,
                        CouponGroup::getCouponType,
                        CouponGroup::getDiscount,
                        CouponGroup::getCouponGroupStatus
                )
                .contains(
                        "한식 밀키트 10% 할인 쿠폰",
                        Duration.ofDays(3),
                        LocalDate.of(3023, 12, 31),
                        17000,
                        CouponTarget.from(ProductCategory.KOREAN),
                        CouponType.RATED,
                        15,
                        CouponGroupStatus.CREATED
                );
    }

    @Test
    @DisplayName("쿠폰 그룹을 생성하면 쿠폰 그룹 상태는 CREATED 이다.")
    void couponGroupStatusCreated() {
        CouponGroup couponGroup = getDefaultCouponGroup();

        assertThat(couponGroup.getCouponGroupStatus()).isEqualTo(CouponGroupStatus.CREATED);
    }

    @Test
    @DisplayName("쿠폰 그룹을 배포하면 쿠폰 그룹 상태는 DEPLOY 이다.")
    void couponGroupStatusCDeploy() {
        CouponGroup couponGroup = getDefaultCouponGroup();

        couponGroup.deploy();

        assertThat(couponGroup.isDeployStatus()).isTrue();
    }

    @Test
    @DisplayName("쿠폰 그룹 발급을 모두 완료하거나 쿠폰 그룹 비활성화하고 싶다면 쿠폰 그룹 상태는 SHUT_DOWN 이다.")
    void couponGroupShowDown() {
        CouponGroup couponGroup = getDefaultCouponGroup();
        couponGroup.deploy();

        couponGroup.shutDown();

        assertThat(couponGroup.getCouponGroupStatus()).isEqualTo(CouponGroupStatus.SHUT_DOWN);
    }

    @Test
    @DisplayName("쿠폰 그룹 발급을 모두 완료하거나 쿠폰 그룹 비활성화하고 싶다면 쿠폰 그룹 상태는 배포 상태여야한다.")
    void couponGroupShowDownFail() {
        CouponGroup couponGroup = getDefaultCouponGroup();

        couponGroup.shutDown();

        assertThat(couponGroup.getCouponGroupStatus()).isNotEqualTo(CouponGroupStatus.SHUT_DOWN);
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰 그룹'을 생성할 때 쿠폰 이름이 없다면 실패한다.")
    void createCouponGroupNameFail() {

        assertThatCode(this::getCouponGroupWithoutName)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 이름은 필수 입니다.");
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰 그룹'을 생성할 때 쿠폰 만료일이 없다면 실패한다.")
    void createCouponGroupEndDateFail() {

        assertThatCode(this::getCouponGroupWithoutEndDate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 만료일은 필수 입니다.");
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰 그룹'을 생성할 때 쿠폰 적용 대상이 없다면 실패한다.")
    void createCouponGroupTargetFail() {

        assertThatCode(this::getCouponGroupWithoutCouponTarget)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 적용 대상은 필수 값입니다.");
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰 그룹'을 생성할 때 쿠폰 타입이 없다면 실패한다.")
    void createCouponGroupTypeFail() {

        assertThatCode(this::getCouponGroupWithoutType)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 타입은 필수 값입니다.");
    }

    @Test
    @DisplayName("쿠폰을 생성할 '쿠폰 그룹'을 생성할 때 할인 값은 양수여야한다. 실패한다.")
    void createCouponGroupDiscountFail() {

        assertThatCode(this::getCouponGroupWithoutDiscount)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인 금액 또는 할인률은 양수여야합니다.");
    }

    @Test
    @DisplayName("쿠폰 그룹에 쿠폰 배포 설정이 없다면 생성하는데 실패한다.")
    void createCouponGroupFailBecauseOfDeploy() {

        assertThatCode(() -> getCouponGroupWithoutCouponDeploy())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 배포 설정은 필수 입니다.");
    }

    @Test
    @DisplayName("쿠폰 그룹이 배포 상태가 아니라면 쿠폰을 발급하는데 실패한다.")
    void makeCouponFailBecauseOfDeploy() {
        CouponGroup couponGroup = getCouponGroup(CouponType.RATED, 10);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);

        assertThatCode(() -> couponGroup.issueCoupon(memberId, now))
                .isInstanceOf(IssueCouponException.class);
    }

    @Test
    @DisplayName("정률 쿠폰 그룹로 쿠폰을 생성한다.")
    void makeRateCoupon() {
        CouponGroup couponGroup = getCouponGroup(CouponType.RATED, 10);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);
        couponGroup.deploy();

        Coupon coupon = couponGroup.issueCoupon(memberId, now);

        assertThat(coupon)
                .extracting(Coupon::getDiscount, Coupon::getMemberId)
                .contains(10, memberId);
    }

    @Test
    @DisplayName("정액 쿠폰 그룹로 쿠폰을 생성한다.")
    void makeFixedCoupon() {
        CouponGroup couponGroup = getCouponGroup(CouponType.FIXED, 1000);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3023, 12, 31);
        couponGroup.deploy();

        Coupon coupon = couponGroup.issueCoupon(memberId, now);

        assertThat(coupon)
                .extracting(Coupon::getDiscount, Coupon::getMemberId)
                .contains(1000, memberId);
    }

    @Test
    @DisplayName("쿠폰 그룹이 만료되면 쿠폰을 생성하는데 실패한다.")
    void makeFixedCouponFail() {
        CouponGroup couponGroup = getCouponGroup(CouponType.FIXED, 1000);
        Long memberId = 1L;
        LocalDate now = LocalDate.of(3033, 12, 31);
        couponGroup.deploy();

        assertThatCode(() -> couponGroup.issueCoupon(memberId, now))
                .isInstanceOf(CouponGroupExpiredException.class)
                .hasMessage("쿠폰 그룹이 만료되었습니다.");
    }

    @Test
    @DisplayName("쿠폰 그룹이 제한 있는 배포 타입인 경우 isLimitType 이 true 이다.")
    void isLimitTypeTrue() {
        CouponGroup couponGroup = getCouponGroupBuilder().couponDeploy(CouponDeploy.getDeployLimitInstance(100))
                .build();

        boolean limitType = couponGroup.isLimitType();

        assertThat(limitType).isTrue();
    }

    @Test
    @DisplayName("쿠폰 그룹이 제한 있는 배포 타입이 아닌 경우 isLimitType 이 false 이다.")
    void isLimitTypeFalse() {
        CouponGroup couponGroup = getCouponGroupBuilder().couponDeploy(CouponDeploy.getDeployNoLimitInstance()).build();

        boolean limitType = couponGroup.isLimitType();

        assertThat(limitType).isFalse();
    }

    private CouponGroup getCouponGroupWithoutCouponDeploy() {
        return CouponGroup.builder()
                .name("default")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .build();
    }

    private CouponGroup getCouponGroupWithoutName() {
        return CouponGroup.builder()
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance())
                .build();
    }

    private CouponGroup getCouponGroupWithoutCouponTarget() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .couponType(CouponType.RATED)
                .discount(15)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance())
                .build();
    }

    private CouponGroup getCouponGroupWithoutEndDate() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance())
                .build();
    }

    private CouponGroup getCouponGroupWithoutType() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .discount(15)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance())
                .build();
    }

    private CouponGroup getCouponGroupWithoutDiscount() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(2023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance())
                .build();
    }

    private CouponGroup getCouponGroup(final CouponType couponType, final int discount) {
        return getCouponGroupBuilder()
                .couponType(couponType)
                .discount(discount)
                .build();
    }

    private CouponGroup getDefaultCouponGroup() {
        return getCouponGroupBuilder()
                .build();
    }

    private CouponGroup.CouponGroupBuilder getCouponGroupBuilder() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance());
    }
}
