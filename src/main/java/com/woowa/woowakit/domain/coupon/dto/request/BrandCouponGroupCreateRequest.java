package com.woowa.woowakit.domain.coupon.dto.request;

import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BrandCouponGroupCreateRequest extends CouponGroupCreateRequest {

    @NotNull(message = "쿠폰 적용 대상은 필수 값입니다.")
    private ProductBrand brand;

    private BrandCouponGroupCreateRequest(
            final String name,
            final Long durationDay,
            final LocalDate endDate,
            final CouponType couponType,
            final ProductBrand productBrand,
            final int minimumOrderAmount,
            final int discount,
            final CouponDeploy couponDeploy
    ) {
        super(name, durationDay, endDate, couponType, minimumOrderAmount, discount, couponDeploy);
        this.brand = productBrand;
    }

    public static BrandCouponGroupCreateRequest of(
            final String name,
            final Long durationDay,
            final LocalDate localDate,
            final CouponType couponType,
            final ProductBrand productBrand,
            final int minimumOrderAmount,
            final int discount,
            final CouponDeploy couponDeploy
    ) {
        return new BrandCouponGroupCreateRequest(
                name,
                durationDay,
                localDate,
                couponType,
                productBrand,
                minimumOrderAmount,
                discount,
                couponDeploy
        );
    }
}
