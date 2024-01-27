package com.woowa.woowakit.domain.order.domain;

import com.woowa.woowakit.domain.cart.exception.ProductNotExistException;
import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponApplicableToOrderItemService {

    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;

    public List<CouponGroupOrderItem> getCouponOrderItems(
            final Long memberId,
            final LocalDate now,
            final List<OrderItem> orderItems
    ) {
        List<Coupon> coupons = couponRepository.findCoupon(memberId, now);
        List<Product> products = productRepository.findByIds(orderItems.stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toList()));

        return orderItems.stream()
                .map(orderItem -> new CouponGroupOrderItem(getApplicableCoupons(coupons, products, orderItem), orderItem))
                .collect(Collectors.toList());
    }

    private List<Coupon> getApplicableCoupons(
            final List<Coupon> coupons,
            final List<Product> products,
            final OrderItem orderItem
    ) {
        return coupons.stream()
                .filter(coupon -> coupon.isApplicable(getProduct(products, orderItem.getProductId())))
                .collect(Collectors.toList());
    }

    private Product getProduct(final List<Product> products, final Long productId) {
        return products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElseThrow(ProductNotExistException::new);
    }
}
