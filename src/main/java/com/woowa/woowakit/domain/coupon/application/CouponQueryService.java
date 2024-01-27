package com.woowa.woowakit.domain.coupon.application;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.exception.NotFoundCouponException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponQueryService {

    private final CouponRepository couponRepository;

    public Coupon getCoupon(final Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(NotFoundCouponException::new);
    }

    public List<Coupon> getCouponsByMember(final Long memberId, final LocalDate now) {
        return couponRepository.findCoupon(memberId, now);
    }
}
