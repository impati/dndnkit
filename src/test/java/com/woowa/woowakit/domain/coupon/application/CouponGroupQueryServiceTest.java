package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.CouponDeploy;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.domain.IssueType;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponGroupQueryServiceTest {

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponGroupQueryService couponGroupQueryService;

    @AfterEach
    void tearDown() {
        couponGroupRepository.deleteAll();
    }

    @Test
    @DisplayName("유효한 쿠폰틀을 조회한다.")
    void getAvailableCouponGroups() {
        couponGroupRepository.save(getCouponGroupBuilder()
                .endDate(LocalDate.of(3023, 10, 16))
                .build());
        couponGroupRepository.save(getCouponGroupBuilder()
                .endDate(LocalDate.of(3023, 10, 17))
                .build());
        couponGroupRepository.save(getCouponGroupBuilder()
                .endDate(LocalDate.of(3023, 10, 18))
                .build());
        LocalDate now = LocalDate.of(3023, 10, 17);

        List<CouponGroup> couponGroups = couponGroupQueryService.getCouponGroups(now);

        assertThat(couponGroups).hasSize(2);
    }

    private CouponGroup.CouponGroupBuilder getCouponGroupBuilder() {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .couponDeploy(CouponDeploy.getDeployNoLimitInstance())
                .issueType(IssueType.REPEATABLE)
                .discount(15);
    }
}
