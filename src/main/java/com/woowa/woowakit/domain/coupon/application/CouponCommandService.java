package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponIssuableDecider;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import java.time.LocalDate;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponCommandService {

    private final CouponRepository couponRepository;
    private final CouponGroupQueryService couponGroupQueryService;
    private final CouponIssuableDecider couponIssuableDecider;
    private final CouponDeployAmountRepository couponDeployAmountCacheRepository;

    public Long create(
            final Long memberId,
            final Long couponGroupId,
            final LocalDate now
    ) {
        CouponGroup couponGroup = couponGroupQueryService.getCouponGroup(couponGroupId);
        couponIssuableDecider.validateIssuable(couponGroup);
        try {
            return issueCoupon(memberId, couponGroup, now);
        } catch (Exception exception) {
            couponDeployAmountCacheRepository.increase(couponGroup);
            throw exception;
        }
    }

    private Long issueCoupon(
            final Long memberId,
            final CouponGroup couponGroup,
            final LocalDate now
    ) {
        couponDeployAmountCacheRepository.decrease(couponGroup);
        Coupon coupon = couponGroup.issueCoupon(memberId, now);

        return couponRepository.save(coupon).getId();
    }
}
