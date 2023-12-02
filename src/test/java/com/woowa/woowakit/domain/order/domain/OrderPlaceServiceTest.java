package com.woowa.woowakit.domain.order.domain;

import com.woowa.woowakit.domain.cart.domain.CartItemRepository;
import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponGroupRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.order.exception.OrderNotFoundException;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.woowa.woowakit.domain.fixture.CartItemFixture.getCartItemBuilder;
import static com.woowa.woowakit.domain.fixture.CouponFixture.getDefaultCouponGroupBuilder;
import static com.woowa.woowakit.domain.fixture.OrderFixture.getOrderBuilder;
import static com.woowa.woowakit.domain.fixture.OrderFixture.getOrderItemBuilder;
import static com.woowa.woowakit.domain.fixture.ProductFixture.getInStockProductBuilder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderPlaceServiceTest {

    @Autowired
    private OrderPlaceService orderPlaceService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        couponGroupRepository.deleteAll();
        couponRepository.deleteAll();
    }

    @Test
    @DisplayName("주문시 주문 상태가 PLACE 가 되고 재고 감소와 장바구니를 비운다.")
    void place() {
        Long memberId = 99L;
        Product productA = getProduct(100, "ProductA", 15000);
        Product productB = getProduct(200, "ProductB", 30000);
        addCartItem(memberId, productA, 90);
        addCartItem(memberId, productB, 110);
        Order order = getPersistedOrder(memberId);

        orderPlaceService.place(memberId, order.getId(), List.of());

        assertThat(getOrderById(order.getId()).getOrderStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(getProductById(productA.getId()).getQuantity()).isEqualTo(10);
        assertThat(getProductById(productB.getId()).getQuantity()).isEqualTo(90);
        assertThat(cartItemRepository.findCartItemByMemberId(memberId)).isEmpty();
    }

    @Test
    @DisplayName("쿠폰이 적용된 주문 아이템이 있는 경우 쿠폰에 적용된 금액만큼 할인된다.")
    void placeWithFixedCoupon() {
        LocalDate now = LocalDate.of(3023, 12, 31);
        Long memberId = 99L;
        Product productA = getProduct(100, "ProductA", 15000);
        Product productB = getProduct(200, "ProductB", 30000);
        Coupon coupon = saveCoupon(memberId, CouponType.FIXED, 2000, CouponTarget.all(), now);
        addCartItem(memberId, productA, 90);
        addCartItem(memberId, productB, 110);
        Order order = getPersistedOrder(memberId);
        OrderItem orderItem = getFirstOrderItem(order);
        CouponAppliedOrderItem couponAppliedOrderItem = CouponAppliedOrderItem.of(coupon, orderItem);

        orderPlaceService.place(memberId, order.getId(), List.of(couponAppliedOrderItem));

        assertThat(getOrderById(order.getId()).getTotalPrice())
                .isEqualTo(productA.getPrice() * 90 + productB.getPrice() * 110 - coupon.getDiscount());
    }

    @Test
    @DisplayName("쿠폰이 적용된 주문 아이템이 있는 경우 쿠폰에 적용된 할인률만큼 할인된다.")
    void placeWithRatedCoupon() {
        LocalDate now = LocalDate.of(3023, 12, 31);
        Long memberId = 99L;
        Product productA = getProduct(100, "ProductA", 15000);
        Product productB = getProduct(200, "ProductB", 30000);
        Coupon coupon = saveCoupon(memberId, CouponType.RATED, 10, CouponTarget.all(), now);
        addCartItem(memberId, productA, 90);
        addCartItem(memberId, productB, 110);
        Order order = getPersistedOrder(memberId);
        OrderItem orderItem = getFirstOrderItem(order);
        CouponAppliedOrderItem couponAppliedOrderItem = CouponAppliedOrderItem.of(coupon, orderItem);

        orderPlaceService.place(memberId, order.getId(), List.of(couponAppliedOrderItem));

        long totalPrice = (productA.getPrice() * 90 + productB.getPrice() * 110);
        totalPrice -= orderItem.getPrice() * (10 / 100d);

        assertThat(getOrderById(order.getId()).getTotalPrice()).isEqualTo(totalPrice);
    }

    private static OrderItem getFirstOrderItem(final Order order) {
        return order.getOrderItems().get(0);
    }

    private Order getOrderById(final Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    private Product getProductById(final Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(OrderNotFoundException::new);
    }

    private void addCartItem(
            final Long memberId,
            final Product product,
            final long quantity
    ) {
        cartItemRepository.save(getCartItemBuilder()
                .memberId(memberId)
                .product(product)
                .quantity(quantity)
                .build());
    }

    private Order getPersistedOrder(final Long memberId) {
        List<OrderItem> orderItems = cartItemRepository.findCartItemByMemberId(memberId).stream()
                .map(cartItemSpecification -> getOrderItemBuilder()
                        .price(cartItemSpecification.getProductPrice())
                        .productId(cartItemSpecification.getProductId())
                        .quantity(cartItemSpecification.getQuantity())
                        .build())
                .collect(Collectors.toList());
        return orderRepository.save(getOrderBuilder()
                .memberId(memberId)
                .orderItems(orderItems)
                .build());
    }

    private Product getProduct(
            final long quantity,
            final String name,
            final long price
    ) {
        return productRepository.save(getInStockProductBuilder()
                .quantity(quantity)
                .name(name)
                .price(price)
                .build());
    }

    private Coupon saveCoupon(
            final Long memberId,
            final CouponType couponType,
            final int discount,
            final CouponTarget couponTarget,
            final LocalDate now
    ) {
        CouponGroup couponGroup = saveCouponGroup(couponTarget, couponType, discount);

        return couponRepository.save(couponGroup.makeCoupon(memberId, now));
    }

    private CouponGroup saveCouponGroup(
            final CouponTarget couponTarget,
            final CouponType couponType,
            final int discount
    ) {
        return couponGroupRepository.save(getDefaultCouponGroupBuilder()
                .couponType(couponType)
                .minimumOrderAmount(17000)
                .discount(discount)
                .couponTarget(couponTarget)
                .build());
    }
}
