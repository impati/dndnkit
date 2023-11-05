package com.woowa.woowakit.domain.order.domain.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.order.domain.CouponAppliedOrderItem;
import com.woowa.woowakit.domain.order.domain.OrderItem;
import com.woowa.woowakit.domain.order.domain.OrderItemRepository;
import com.woowa.woowakit.domain.order.dto.request.CouponAppliedOrderItemRequest;
import com.woowa.woowakit.domain.order.exception.InvalidCouponException;
import com.woowa.woowakit.domain.order.exception.NotFoundCouponException;
import com.woowa.woowakit.domain.order.exception.NotFoundOrderItemException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponAppliedOrderItemMapper {

	private final CouponRepository couponRepository;
	private final OrderItemRepository orderItemRepository;

	public List<CouponAppliedOrderItem> mapFrom(
		final Long memberId,
		final List<CouponAppliedOrderItemRequest> request
	) {
		return request.stream()
			.map(couponAppliedOrderItem -> CouponAppliedOrderItem.of(
				getCoupon(couponAppliedOrderItem.getCouponId(), memberId),
				getOrderItem(couponAppliedOrderItem.getOrderItemId())))
			.collect(Collectors.toList());
	}

	private OrderItem getOrderItem(final Long orderItemId) {
		return orderItemRepository.findById(orderItemId)
			.orElseThrow(NotFoundOrderItemException::new);
	}

	private Coupon getCoupon(final Long couponId, final Long memberId) {
		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(NotFoundCouponException::new);

		validateCoupon(memberId, coupon);
		return coupon;
	}

	private void validateCoupon(final Long memberId, final Coupon coupon) {
		if (!coupon.isOwner(memberId)) {
			throw new InvalidCouponException();
		}
	}
}
