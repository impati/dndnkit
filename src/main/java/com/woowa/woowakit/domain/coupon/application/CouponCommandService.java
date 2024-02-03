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
        validateIssueType(memberId, couponGroup);

        Coupon coupon = couponGroup.issueCoupon(memberId, now);
        Long persistCouponId = decreaseDeployAmountAndSaveCoupon(couponGroup, coupon);

        sendIssueEvent(couponGroup);
        return persistCouponId;
    }

    private void validateIssueType(final Long memberId, final CouponGroup couponGroup) {
        CouponHistory couponHistory = CouponHistory.of(couponGroup, couponRepository.getCouponByCouponGroup(couponGroup, memberId));
        couponHistory.validateIssueCoupon();
    }

    private Long decreaseDeployAmountAndSaveCoupon(final CouponGroup couponGroup, final Coupon coupon) {
        try {
            couponDeployAmountManager.decreaseDeployAmount(couponGroup);
            return couponRepository.save(coupon).getId();
        } catch (Exception exception) {
            couponDeployAmountManager.increase(couponGroup);
            throw exception;
        }
    }

    private void sendIssueEvent(final CouponGroup couponGroup) {
        applicationEventPublisher.publishEvent(new CouponIssueEvent(this, couponGroup));
    }
}
