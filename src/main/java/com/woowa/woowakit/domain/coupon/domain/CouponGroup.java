package com.woowa.woowakit.domain.coupon.domain;

import com.woowa.woowakit.domain.coupon.exception.CouponGroupExpiredException;
import com.woowa.woowakit.domain.coupon.exception.IssueCouponException;
import com.woowa.woowakit.domain.model.BaseEntity;
import com.woowa.woowakit.domain.model.ExpiryDate;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@Entity
@Table(name = "coupon_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "duration")
    private Long duration;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "end_date"))
    private ExpiryDate endDate;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "minimum_order_amount"))
    private MinimumOrderAmount minimumOrderAmount;

    @Embedded
    private CouponTarget couponTarget;

    @Embedded
    private CouponDeploy couponDeploy;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type")
    private CouponType couponType;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_group_status")
    private CouponGroupStatus couponGroupStatus;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "discount"))
    private Discount discount;

    @Builder
    private CouponGroup(
            final String name,
            final Duration duration,
            final LocalDate endDate,
            final int minimumOrderAmount,
            final CouponTarget couponTarget,
            final CouponType couponType,
            final int discount,
            final CouponDeploy couponDeploy
    ) {
        Assert.hasText(name, "쿠폰 이름은 필수 입니다.");
        Assert.notNull(duration, "쿠폰 유효기간은 필수 입니다.");
        Assert.notNull(endDate, "쿠폰 만료일은 필수 입니다.");
        Assert.notNull(couponDeploy, "쿠폰 배포 설정은 필수 입니다.");
        Assert.notNull(couponTarget, "쿠폰 적용 대상은 필수 값입니다.");
        Assert.notNull(couponType, "쿠폰 타입은 필수 값입니다.");
        Assert.isTrue(discount > 0, "할인 금액 또는 할인률은 양수여야합니다.");
        this.name = name;
        this.duration = duration.toDays();
        this.endDate = ExpiryDate.from(endDate);
        this.minimumOrderAmount = MinimumOrderAmount.from(minimumOrderAmount);
        this.couponTarget = couponTarget;
        this.couponType = couponType;
        this.discount = couponType.getDiscount(discount, minimumOrderAmount);
        this.couponDeploy = couponDeploy;
        this.couponGroupStatus = CouponGroupStatus.CREATED;
    }

    public void deploy() {
        this.couponGroupStatus = CouponGroupStatus.DEPLOY;
    }

    public void shutDown() {
        if (this.couponGroupStatus == CouponGroupStatus.DEPLOY) {
            this.couponGroupStatus = CouponGroupStatus.SHUT_DOWN;
        }
    }

    public boolean isLimitType() {
        return this.couponDeploy.getCouponDeployType() == CouponDeployType.LIMIT;
    }

    public boolean isDeployStatus() {
        return this.couponGroupStatus == CouponGroupStatus.DEPLOY;
    }

    public Coupon issueCoupon(final Long memberId, final LocalDate now) {
        validateIssueCouponGroup(now);
        return Coupon.builder()
                .couponType(couponType)
                .discount(discount.value)
                .expiryDate(now.plusDays(duration))
                .couponTarget(couponTarget)
                .name(name)
                .memberId(memberId)
                .minimumOrderAmount(minimumOrderAmount.getValue())
                .build();
    }

    private void validateIssueCouponGroup(final LocalDate now) {
        if (!(now.isBefore(endDate.getValue()) || now.isEqual(endDate.getValue()))) {
            throw new CouponGroupExpiredException();
        }
        if (couponGroupStatus != CouponGroupStatus.DEPLOY) {
            throw new IssueCouponException();
        }
    }

    public int getDeployAmount() {
        return this.getCouponDeploy().getDeployAmount();
    }

    public Duration getDuration() {
        return Duration.ofDays(duration);
    }

    public LocalDate getEndDate() {
        return endDate.getValue();
    }

    public int getMinimumOrderAmount() {
        return minimumOrderAmount.getValue();
    }

    public int getDiscount() {
        return discount.getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CouponGroup)) {
            return false;
        }

        final CouponGroup coupon = (CouponGroup) o;
        return this.getId() != null && Objects.equals(getId(), coupon.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
