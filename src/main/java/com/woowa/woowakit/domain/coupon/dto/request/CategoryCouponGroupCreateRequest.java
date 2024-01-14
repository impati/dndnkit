package com.woowa.woowakit.domain.coupon.dto.request;

import com.woowa.woowakit.domain.coupon.domain.CouponDeployType;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.domain.IssueType;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CategoryCouponGroupCreateRequest extends CouponGroupCreateRequest {

    @NotNull(message = "쿠폰 적용 대상은 필수 값입니다.")
    private ProductCategory category;

    private CategoryCouponGroupCreateRequest(
            final String name,
            final Long durationDay,
            final LocalDate endDate,
            final CouponType couponType,
            final ProductCategory productCategory,
            final int minimumOrderAmount,
            final int discount,
            final CouponDeployType couponDeployType,
            final Integer amount,
            final IssueType issueType
    ) {
        super(name, durationDay, endDate, couponType, minimumOrderAmount, discount, couponDeployType, amount, issueType);
        this.category = productCategory;
    }

    public static CategoryCouponGroupCreateRequest of(
            final String name,
            final Long durationDay,
            final LocalDate localDate,
            final CouponType couponType,
            final ProductCategory productCategory,
            final int minimumOrderAmount,
            final int discount,
            final CouponDeployType couponDeployType,
            final Integer amount,
            final IssueType issueType
    ) {
        return new CategoryCouponGroupCreateRequest(
                name,
                durationDay,
                localDate,
                couponType,
                productCategory,
                minimumOrderAmount,
                discount,
                couponDeployType,
                amount,
                issueType
        );
    }
}
