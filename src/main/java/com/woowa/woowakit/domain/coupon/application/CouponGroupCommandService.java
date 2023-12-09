package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.CouponDeployAmountRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.dto.request.CouponGroupCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponGroupCommandService {

    private final CouponGroupQueryService couponGroupQueryService;
    private final CouponGroupRepository couponGroupRepository;
    private final CouponDeployAmountRepository couponDeployAmountRepository;

    public Long create(final CouponTarget couponTarget, final CouponGroupCreateRequest request) {
        final CouponGroup couponGroup = request.toEntity(couponTarget);

        return couponGroupRepository.save(couponGroup).getId();
    }

    public void deploy(final Long couponGroupId) {
        CouponGroup couponGroup = couponGroupQueryService.getCouponGroup(couponGroupId);

        couponGroup.deploy();

        if (couponGroup.isLimitType()) {
            couponDeployAmountRepository.deploy(couponGroup.getId(), couponGroup.getDeployAmount());
        }
    }
}
