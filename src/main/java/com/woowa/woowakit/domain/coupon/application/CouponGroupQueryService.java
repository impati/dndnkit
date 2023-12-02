package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.exception.NotFoundCouponGroupException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponGroupQueryService {

    private final CouponGroupRepository couponGroupRepository;

    public CouponGroup getCouponGroup(final Long couponGroupId) {
        return couponGroupRepository.findById(couponGroupId)
                .orElseThrow(NotFoundCouponGroupException::new);
    }

    public List<CouponGroup> getCouponGroups(final LocalDate now) {
        return couponGroupRepository.findAvailableCouponGroup(now);
    }
}
