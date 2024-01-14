package com.woowa.woowakit.domain.coupon.dto.request;

import com.woowa.woowakit.domain.coupon.domain.CouponDeployType;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.domain.IssueType;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProductCouponGroupCreateRequest extends CouponGroupCreateRequest {

    @NotNull(message = "쿠폰 적용 대상은 필수 값입니다.")
    private Long productId;

    private ProductCouponGroupCreateRequest(
            final String name,
            final Long durationDay,
            final LocalDate endDate,
            final CouponType couponType,
            final Long productId,
            final int minimumOrderAmount,
            final int discount,
            final CouponDeployType couponDeployType,
            final Integer amount,
            final IssueType issueType
    ) {
        super(name, durationDay, endDate, couponType, minimumOrderAmount, discount, couponDeployType, amount, issueType);
        this.productId = productId;
    }

    public static ProductCouponGroupCreateRequest of(
            final String name,
            final Long durationDay,
            final LocalDate localDate,
            final CouponType couponType,
            final Long productId,
            final int minimumOrderAmount,
            final int discount,
            final CouponDeployType couponDeployType,
            final Integer couponDeployAmount,
            final IssueType issueType
    ) {
        return new ProductCouponGroupCreateRequest(
                name,
                durationDay,
                localDate,
                couponType,
                productId,
                minimumOrderAmount,
                discount,
                couponDeployType,
                couponDeployAmount,
                issueType
        );
    }
}
