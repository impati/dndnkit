package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountManager;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponHistory;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.event.CouponIssueEvent;
import java.time.LocalDate;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponCommandService {

    private final CouponRepository couponRepository;
    private final CouponGroupQueryService couponGroupQueryService;
    private final CouponDeployAmountManager couponDeployAmountManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Long create(
            final Long memberId,
            final Long couponGroupId,
            final LocalDate now
    ) {
        CouponGroup couponGroup = couponGroupQueryService.getCouponGroup(couponGroupId);
        CouponHistory couponHistory = CouponHistory.of(couponGroup, couponRepository.getCouponByCouponGroup(couponGroup, memberId));
        couponHistory.validateIssueCoupon();
        Coupon coupon = couponGroup.issueCoupon(memberId, now);
        try {
            couponDeployAmountManager.decreaseDeployAmount(couponGroup);
            applicationEventPublisher.publishEvent(new CouponIssueEvent(this, couponGroup));

            return couponRepository.save(coupon).getId();
        } catch (Exception exception) {
            couponDeployAmountManager.increase(couponGroup);
            throw exception;
        }
    }
}
