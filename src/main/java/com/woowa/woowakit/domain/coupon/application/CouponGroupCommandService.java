package com.woowa.woowakit.domain.coupon.application;

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

    private final CouponGroupRepository couponGroupRepository;

    public Long create(final CouponTarget couponTarget, final CouponGroupCreateRequest request) {
        final CouponGroup couponGroup = request.toEntity(couponTarget);

        return couponGroupRepository.save(couponGroup).getId();
    }
}
