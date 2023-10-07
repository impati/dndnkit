package com.woowa.woowakit.domain.coupon.domain;

import java.time.LocalDate;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;

import org.springframework.util.Assert;

import com.woowa.woowakit.domain.model.ExpiryDate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("FIXED")
public class FixedCoupon extends Coupon {

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "discount_amount"))
	private DiscountAmount discountAmount;

	@Builder
	private FixedCoupon(
		final Long memberId,
		final String name,
		final LocalDate expiryDate,
		final int minimumOrderAmount,
		final CouponTarget couponTarget,
		final int discountAmount
	) {
		Assert.notNull(memberId, "사용자 ID 는 필수입니다.");
		Assert.hasText(name, "쿠폰 이름은 필수 입니다.");
		Assert.notNull(expiryDate, "쿠폰 만료일은 필수입니다.");
		Assert.notNull(couponTarget, "쿠폰 적용 대상 설정은 필수입니다.");
		this.memberId = memberId;
		this.name = name;
		this.expiryDate = ExpiryDate.from(expiryDate);
		this.minimumOrderAmount = MinimumOrderAmount.from(minimumOrderAmount);
		this.couponTarget = couponTarget;
		this.discountAmount = DiscountAmount.of(discountAmount, minimumOrderAmount);
	}

	public int getDiscountAmount() {
		return discountAmount.getValue();
	}
}
