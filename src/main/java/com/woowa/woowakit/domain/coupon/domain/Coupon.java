package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.model.BaseEntity;
import com.woowa.woowakit.domain.model.ExpiryDate;
import com.woowa.woowakit.domain.product.domain.Product;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@Entity
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_group_id")
    private CouponGroup couponGroup;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "expiry_date"))
    private ExpiryDate expiryDate;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "minimum_order_amount"))
    private MinimumOrderAmount minimumOrderAmount;

    @Embedded
    private CouponTarget couponTarget;

    @Column(name = "member_id")
    private Long memberId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "discount"))
    private Discount discount;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type")
    private CouponType couponType;

    @Column(name = "enabled")
    private boolean enabled;

    @Builder
    private Coupon(
            final Long memberId,
            final String name,
            final CouponGroup couponGroup,
            final LocalDate expiryDate,
            final int minimumOrderAmount,
            final CouponTarget couponTarget,
            final CouponType couponType,
            final int discount
    ) {
        Assert.notNull(memberId, "사용자 ID 는 필수입니다.");
        Assert.hasText(name, "쿠폰 이름은 필수 입니다.");
        Assert.notNull(couponGroup, "쿠폰 그룹은 필수 입니다.");
        Assert.notNull(expiryDate, "쿠폰 만료일은 필수입니다.");
        Assert.notNull(couponTarget, "쿠폰 적용 대상 설정은 필수입니다.");
        Assert.notNull(couponType, "쿠폰 타입 설정은 필수입니다.");
        this.memberId = memberId;
        this.name = name;
        this.couponGroup = couponGroup;
        this.expiryDate = ExpiryDate.from(expiryDate);
        this.minimumOrderAmount = MinimumOrderAmount.from(minimumOrderAmount);
        this.couponTarget = couponTarget;
        this.couponType = couponType;
        this.discount = couponType.getDiscount(discount, minimumOrderAmount);
        this.enabled = true;
    }

    public boolean isOwner(final Long memberId) {
        return Objects.equals(this.memberId, memberId);
    }

    public boolean isApplicable(final Product product) {
        return couponTarget.isApplicable(product);
    }

    public LocalDate getExpiryDate() {
        return expiryDate.getValue();
    }

    public int getMinimumOrderAmount() {
        return minimumOrderAmount.getValue();
    }

    public int getDiscount() {
        return discount.getValue();
    }

    public int computeDiscountPrice(final long price) {
        if (this.couponType == CouponType.FIXED) {
            return discount.getValue();
        }

        return (int) (price * (discount.getValue() / 100d));
    }

    public void used() {
        this.enabled = false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coupon)) {
            return false;
        }

        final Coupon coupon = (Coupon) o;
        return this.getId() != null && Objects.equals(getId(), coupon.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
