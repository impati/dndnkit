package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.CouponIssueTypeException;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class CouponHistoryTest {

    @Test
    @DisplayName("쿠폰 그룹 발급 타입이 반복 불가 타입이고 발급 받은 이력이 있다면 validate 에 실패한다.")
    void validateFailIssueCouponByNO_REPEATABLE() {
        IssueType issueType = IssueType.NO_REPEATABLE;
        CouponGroup couponGroup = getCouponGroupBuilder().issueType(issueType).build();
        couponGroup.deploy();
        Coupon coupon = couponGroup.issueCoupon(1L, LocalDate.of(3023, 12, 31));
        CouponHistory couponHistory = CouponHistory.of(couponGroup, List.of(coupon));

        assertThatCode(couponHistory::validateIssueCoupon)
                .isInstanceOf(CouponIssueTypeException.class)
                .hasMessage("쿠폰 발급에 실패했습니다. 반복 발급이 불가능한 쿠폰 그룹입니다.");
    }

    @Test
    @DisplayName("쿠폰 그룹 발급 타입이 반복 불가 타입이고 발급 받은 이력이 있다면 validate 에 실패한다.")
    void validateFailIssueCouponByNO_REPEATABLE2() {
        IssueType issueType = IssueType.NO_REPEATABLE;
        CouponGroup couponGroup = getCouponGroupBuilder().issueType(issueType).build();
        couponGroup.deploy();
        Coupon coupon = couponGroup.issueCoupon(1L, LocalDate.of(3023, 12, 31));
        coupon.used();
        CouponHistory couponHistory = CouponHistory.of(couponGroup, List.of(coupon));

        assertThatCode(couponHistory::validateIssueCoupon)
                .isInstanceOf(CouponIssueTypeException.class)
                .hasMessage("쿠폰 발급에 실패했습니다. 반복 발급이 불가능한 쿠폰 그룹입니다.");
    }

    @Test
    @DisplayName("쿠폰 그룹 발급 타입이 반복 불가 타입이고 발급 받은 이력이 없다면 validate 에 성공한다.")
    void validateIssueCouponByNO_REPEATABLE() {
        IssueType issueType = IssueType.NO_REPEATABLE;
        CouponGroup couponGroup = getCouponGroupBuilder().issueType(issueType).build();
        couponGroup.deploy();
        CouponHistory couponHistory = CouponHistory.of(couponGroup, List.of());

        assertThatCode(couponHistory::validateIssueCoupon)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("쿠폰 그룹 발급 타입이 사용 후 재발급이고 발급 받은 이력이 없다면 validate 에 성공한다.")
    void validateIssueCouponByRepeatableAfterUsed() {
        IssueType issueType = IssueType.REPEATABLE_AFTER_USED;
        CouponGroup couponGroup = getCouponGroupBuilder().issueType(issueType).build();
        couponGroup.deploy();
        CouponHistory couponHistory = CouponHistory.of(couponGroup, List.of());

        assertThatCode(couponHistory::validateIssueCoupon)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("쿠폰 그룹 발급 타입이 사용 후 재발급이고 발급 받은 이력이 있는데 사용한 경우 validate 에 성공한다.")
    void validateIssueCouponByRepeatableAfterUsed2() {
        IssueType issueType = IssueType.REPEATABLE_AFTER_USED;
        CouponGroup couponGroup = getCouponGroupBuilder().issueType(issueType).build();
        couponGroup.deploy();
        Coupon coupon = couponGroup.issueCoupon(1L, LocalDate.of(3023, 12, 31));
        coupon.used();
        CouponHistory couponHistory = CouponHistory.of(couponGroup, List.of(coupon));

        assertThatCode(couponHistory::validateIssueCoupon)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("쿠폰 그룹 발급 타입이 사용 후 재발급이고 발급 받은 이력이 있는데 사용하지 않은 경우 validate 에 실패한다.")
    void validateIssueFailCouponByRepeatableAfterUsed2() {
        IssueType issueType = IssueType.REPEATABLE_AFTER_USED;
        CouponGroup couponGroup = getCouponGroupBuilder().issueType(issueType).build();
        couponGroup.deploy();
        Coupon coupon = couponGroup.issueCoupon(1L, LocalDate.of(3023, 12, 31));
        CouponHistory couponHistory = CouponHistory.of(couponGroup, List.of(coupon));

        assertThatCode(couponHistory::validateIssueCoupon)
                .isInstanceOf(CouponIssueTypeException.class)
                .hasMessage("쿠폰 발급에 실패했습니다. 사용 후 발급이 가능한 쿠폰 그룹입니다.");
    }

    @Test
    @DisplayName("쿠폰 그룹 발급 타입이 재발급이고 발급 받은 이력이 없다면 validate 에 성공한다.")
    void validateIssue() {
        IssueType issueType = IssueType.REPEATABLE;
        CouponGroup couponGroup = getCouponGroupBuilder().issueType(issueType).build();
        couponGroup.deploy();
        CouponHistory couponHistory = CouponHistory.of(couponGroup, List.of());

        assertThatCode(couponHistory::validateIssueCoupon)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("쿠폰 그룹 발급 타입이 재발급이고 발급 받은 이력이 있는데 사용한 경우 validate 에 성공한다.")
    void validateIssue2() {
        IssueType issueType = IssueType.REPEATABLE;
        CouponGroup couponGroup = getCouponGroupBuilder().issueType(issueType).build();
        couponGroup.deploy();
        Coupon coupon = couponGroup.issueCoupon(1L, LocalDate.of(3023, 12, 31));
        coupon.used();
        CouponHistory couponHistory = CouponHistory.of(couponGroup, List.of(coupon));

        assertThatCode(couponHistory::validateIssueCoupon)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("쿠폰 그룹 발급 타입이 재발급이고 발급 받은 이력이 있는데 사용하지 않은 경우 validate 에 성공한다.")
    void validateIssue3() {
        IssueType issueType = IssueType.REPEATABLE;
        CouponGroup couponGroup = getCouponGroupBuilder().issueType(issueType).build();
        couponGroup.deploy();
        Coupon coupon = couponGroup.issueCoupon(1L, LocalDate.of(3023, 12, 31));
        CouponHistory couponHistory = CouponHistory.of(couponGroup, List.of(coupon));

        assertThatCode(couponHistory::validateIssueCoupon)
                .doesNotThrowAnyException();
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
                .issueType(IssueType.REPEATABLE)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance());
    }
}
