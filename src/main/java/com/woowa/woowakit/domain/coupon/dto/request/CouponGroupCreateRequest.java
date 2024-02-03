package com.woowa.woowakit.domain.coupon.dto.request;

import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponDeployType;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.domain.IssueType;
import java.time.Duration;
import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponGroupCreateRequest {

    @NotBlank(message = "쿠폰 이름은 필수 입니다.")
    protected String name;

    @NotNull(message = "쿠폰 유효 기간은 필수 값입니다.")
    @Min(value = 1, message = "쿠폰 유효 기간은 하루 이상이어야합니다.")
    protected Long durationDay;

    @NotNull(message = "쿠폰 만료 기간은 필수 값입니다.")
    protected LocalDate endDate;

    @NotNull(message = "쿠폰 타입은 필수 값입니다.")
    protected CouponType couponType;

    @Min(value = 1000, message = "최소 주문 금액은 1000원이상입니다.")
    protected int minimumOrderAmount;

    @Min(value = 1, message = "할인 값은 양수여야합니다.")
    protected int discount;

    @NotNull(message = "쿠폰 배포 타입 설정은 필수 입니다.")
    protected CouponDeployType couponDeployType;

    protected Integer couponDeployAmount;

    @NotNull(message = "쿠폰 발급 타입은 필수 입니다.")
    protected IssueType issueType;

    public CouponGroup toEntity(final CouponTarget couponTarget) {
        CouponDeploy couponDeploy = CouponDeploy.getDeployNoLimitInstance();
        if (couponDeployType == CouponDeployType.LIMIT) {
            couponDeploy = CouponDeploy.getDeployLimitInstance(couponDeployAmount);
        }
        return CouponGroup.builder()
                .name(name)
                .duration(Duration.ofDays(durationDay))
                .endDate(endDate)
                .couponType(couponType)
                .minimumOrderAmount(minimumOrderAmount)
                .discount(discount)
                .couponTarget(couponTarget)
                .couponDeploy(couponDeploy)
                .issueType(issueType)
                .build();
    }
}
