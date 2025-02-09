package com.woowa.woowakit.domain.order.domain.mapper;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.fixture.OrderFixture;
import com.woowa.woowakit.domain.order.domain.CouponAppliedOrderItem;
import com.woowa.woowakit.domain.order.domain.OrderItem;
import com.woowa.woowakit.domain.order.domain.OrderItemRepository;
import com.woowa.woowakit.domain.order.dto.request.CouponAppliedOrderItemRequest;
import com.woowa.woowakit.domain.order.exception.InvalidCouponException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.woowa.woowakit.domain.fixture.CouponFixture.getDefaultCouponGroupBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class CouponAppliedOrderItemMapperTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponAppliedOrderItemMapper couponAppliedOrderItemMapper;

    @AfterEach
    void tearDown() {
        orderItemRepository.deleteAll();
        couponGroupRepository.deleteAll();
        couponRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰 ID 와 주문 ID 로 Coupon 과 Order 정보를 가져온다.")
    void mapFrom() {
        Long memberId = 1L;
        Coupon coupon = saveCoupon(memberId);
        OrderItem orderItem = saveOrderItem(coupon);
        CouponAppliedOrderItemRequest request = CouponAppliedOrderItemRequest.of(
                orderItem.getId(),
                orderItem.getCouponId()
        );

        List<CouponAppliedOrderItem> couponAppliedOrderItems = couponAppliedOrderItemMapper.mapFrom(
                memberId,
                List.of(request)
        );

        assertThat(couponAppliedOrderItems)
                .hasSize(1)
                .extracting(CouponAppliedOrderItem::getCoupon, CouponAppliedOrderItem::getOrderItem)
                .contains(tuple(coupon, orderItem));
    }

    @Test
    @DisplayName("쿠폰이 현재 사용자가 가지고 있는 쿠폰이어야합니다.")
    void mapFromValidateMemberCoupon() {
        Long memberId = 1L;
        Coupon coupon = saveCoupon(memberId);
        OrderItem orderItem = saveOrderItem(coupon);
        CouponAppliedOrderItemRequest request = CouponAppliedOrderItemRequest.of(
                orderItem.getId(),
                orderItem.getCouponId()
        );
        Long otherMemberId = 9L;

        assertThatCode(() -> couponAppliedOrderItemMapper.mapFrom(otherMemberId, List.of(request)))
                .isInstanceOf(InvalidCouponException.class);
    }

    @Test
    @DisplayName("쿠폰을 적용한 주문 아이템이 없는 경우")
    void mapFromCaseOfNoRequest() {
        List<CouponAppliedOrderItem> couponAppliedOrderItems = couponAppliedOrderItemMapper.mapFrom(
                1L,
                List.of()
        );

        assertThat(couponAppliedOrderItems).isEmpty();
    }

    private OrderItem saveOrderItem(final Coupon coupon) {
        OrderItem orderItem = orderItemRepository.save(OrderFixture.getOrderItemBuilder().build());

        orderItem.applyCoupon(coupon.getId());

        return orderItem;
    }

    private Coupon saveCoupon(
            final Long memberId
    ) {
        CouponGroup couponGroup = saveDeployedCouponGroup(CouponTarget.all());

        return couponRepository.save(couponGroup.issueCoupon(memberId, LocalDate.of(3023, 12, 31)));
    }

    private CouponGroup saveDeployedCouponGroup(final CouponTarget couponTarget) {
        CouponGroup couponGroup = getDefaultCouponGroupBuilder()
                .couponType(CouponType.FIXED)
                .minimumOrderAmount(1000)
                .discount(1000)
                .couponTarget(couponTarget)
                .build();
        couponGroup.deploy();
        couponGroupRepository.save(couponGroup);
        return couponGroup;
    }
}
