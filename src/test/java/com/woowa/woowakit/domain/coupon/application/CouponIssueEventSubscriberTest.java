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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponIssueEventSubscriberTest {

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponIssueEventSubscriber couponIssueEventSubscriber;

    @Test
    @DisplayName("동시에 쿠폰 그룹 배포 수량 감소 테스트")
    void syncRdsDeployAmount() throws InterruptedException {
        int deployAmount = 5;
        CouponGroup couponGroup = couponGroupRepository.save(getDeployedCouponGroup(
                CouponDeploy.getDeployLimitInstance(deployAmount)
        ));

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    couponIssueEventSubscriber.syncRdsDeployAmount(couponGroup);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        assertThat(couponGroupRepository.findById(couponGroup.getId()).get().getDeployAmount()).isEqualTo(0);
    }

    private CouponGroup getCouponGroup(final CouponDeploy couponDeploy) {
        return CouponGroup.builder()
                .name("한식 밀키트 10% 할인 쿠폰")
                .duration(Duration.ofDays(3))
                .endDate(LocalDate.of(3023, 12, 31))
                .minimumOrderAmount(17000)
                .couponTarget(CouponTarget.from(ProductCategory.KOREAN))
                .couponType(CouponType.RATED)
                .discount(15)
                .couponDeploy(couponDeploy)
                .issueType(IssueType.REPEATABLE)
                .build();
    }

    private CouponGroup getDeployedCouponGroup(final CouponDeploy couponDeploy) {
        CouponGroup couponGroup = getCouponGroup(couponDeploy);
        couponGroup.deploy();

        return couponGroup;
    }
}
