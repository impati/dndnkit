package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponIssuableDecider;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponCommandService {

    private final CouponRepository couponRepository;
    private final CouponGroupQueryService couponGroupQueryService;
    private final CouponDeployAmountRepository couponDeployAmountRepository;
    private final CouponIssuableDecider couponIssuableDecider;

    public Long create(
            final Long memberId,
            final Long couponGroupId,
            final LocalDate now
    ) {
        CouponGroup couponGroup = couponGroupQueryService.getCouponGroup(couponGroupId);
        couponIssuableDecider.validateIssuable(couponGroup);
        if (couponGroup.isLimitType()) {
            couponDeployAmountRepository.decrease(couponGroup);
        }

        Coupon coupon = couponGroup.issueCoupon(memberId, now);

        return couponRepository.save(coupon).getId();
    }
}
