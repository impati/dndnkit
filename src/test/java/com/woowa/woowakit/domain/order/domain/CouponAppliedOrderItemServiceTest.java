package com.woowa.woowakit.domain.order.domain;

import com.woowa.woowakit.domain.auth.domain.Member;
import com.woowa.woowakit.domain.auth.domain.MemberRepository;
import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.fixture.OrderFixture;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import com.woowa.woowakit.domain.product.domain.ProductRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.woowa.woowakit.domain.fixture.CouponFixture.getDefaultCouponGroupBuilder;
import static com.woowa.woowakit.domain.fixture.ProductFixture.getInStockProductBuilder;
import static com.woowa.woowakit.domain.member.fixture.MemberFixture.anMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class CouponAppliedOrderItemServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CouponApplicableToOrderItemService couponApplicableToOrderItemService;

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        productRepository.deleteAll();
        couponRepository.deleteAll();
        couponGroupRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자의 주문 아이템들을 입력으로 주문 아이템에 적용 가능한 쿠폰 목록을 응답한다.- 모든 상품에 적용할 수 있는 쿠폰 케이스")
    void getCouponOrderItems() {
        Member member = saveMember();
        Product koreaProduct = saveProduct(ProductBrand.COOKIT, ProductCategory.KOREAN);
        LocalDate now = LocalDate.of(3023, 10, 22);
        Coupon mokranBrandCoupon = saveCoupon(member, CouponTarget.all(), now);
        OrderItem orderItem = getOrderItem(mokranBrandCoupon.getId(), koreaProduct.getId());
        Order order = saveOrder(member.getId(), orderItem);

        List<CouponGroupOrderItem> CouponGroupOrderItems = couponApplicableToOrderItemService.getCouponOrderItems(
                member.getId(),
                now,
                order.getOrderItems()
        );

        assertThat(CouponGroupOrderItems).hasSize(1);
        assertThat(CouponGroupOrderItems.get(0).getOrderItem()).isEqualTo(orderItem);
        assertThat(CouponGroupOrderItems.get(0).getCoupons())
                .hasSize(1)
                .extracting(Coupon::getMemberId, Coupon::getCouponTarget)
                .contains(tuple(member.getId(), CouponTarget.all()));
    }

    private Order saveOrder(final Long memberId, final OrderItem... orderItems) {
        return orderRepository.save(Order.builder()
                .memberId(memberId)
                .orderItems(List.of(orderItems))
                .build());
    }

    private OrderItem getOrderItem(final Long couponId, final Long productId) {
        OrderItem orderItem = OrderFixture.getOrderItemBuilder()
                .productId(productId)
                .build();

        orderItem.applyCoupon(couponId);

        return orderItem;
    }

    private Product saveProduct(final ProductBrand productBrand, final ProductCategory productCategory) {
        return productRepository.save(getInStockProductBuilder()
                .productBrand(productBrand)
                .productCategories(List.of(productCategory))
                .build());
    }

    private Member saveMember() {
        return memberRepository.save(anMember().build());
    }

    private Coupon saveCoupon(
            final Member member,
            final CouponTarget couponTarget,
            final LocalDate now
    ) {
        CouponGroup couponGroup = saveCouponGroup(couponTarget);

        return couponRepository.save(couponGroup.makeCoupon(member.getId(), now));
    }

    private CouponGroup saveCouponGroup(final CouponTarget couponTarget) {
        return couponGroupRepository.save(getDefaultCouponGroupBuilder()
                .couponTarget(couponTarget)
                .build());
    }
}
