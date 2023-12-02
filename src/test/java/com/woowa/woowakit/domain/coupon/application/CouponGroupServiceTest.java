package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.dto.request.ProductCouponGroupCreateRequest;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponGroupServiceTest {

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponGroupCommandService couponGroupCommandService;

    @Autowired
    private CouponGroupQueryService couponGroupQueryService;

    @AfterEach
    void tearDown() {
        couponRepository.deleteAll();
        couponGroupRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰 적용 대상과 쿠폰 정보로 쿠폰 그룹을 생성하고 ID 를 반환한다.")
    void createCouponGroup() {
        Long productId = 1L;
        CouponTarget couponTarget = CouponTarget.from(productId);
        ProductCouponGroupCreateRequest request = ProductCouponGroupCreateRequest.of(
                "default",
                1L,
                LocalDate.of(3023, 12, 31),
                CouponType.FIXED,
                productId,
                17000,
                15000
        );

        Long CouponGroupId = couponGroupCommandService.create(couponTarget, request);

        assertThat(couponGroupQueryService.getCouponGroup(CouponGroupId))
                .extracting(
                        CouponGroup::getName,
                        CouponGroup::getDuration,
                        CouponGroup::getEndDate,
                        CouponGroup::getMinimumOrderAmount,
                        CouponGroup::getCouponTarget,
                        CouponGroup::getCouponType,
                        CouponGroup::getDiscount)
                .contains(
                        "default",
                        Duration.ofDays(1),
                        LocalDate.of(3023, 12, 31),
                        17000,
                        CouponTarget.from(productId),
                        CouponType.FIXED,
                        15000);
    }
}
