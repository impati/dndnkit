package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.event.CouponIssueEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponIssueEventSubscriber {

    private final CouponGroupRepository couponGroupRepository;

    @Async("syncDeployAmountExecutor")
    @Transactional
    @TransactionalEventListener(value = CouponIssueEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void syncRdsDeployAmount(CouponIssueEvent couponIssueEvent) {
        CouponGroup couponGroup = couponIssueEvent.getCouponGroup();

        syncRdsDeployAmount(couponGroup);
    }

    @Transactional
    public void syncRdsDeployAmount(CouponGroup couponGroup) {
        couponGroupRepository.decreaseDeployAmount(couponGroup.getId());
    }
}
