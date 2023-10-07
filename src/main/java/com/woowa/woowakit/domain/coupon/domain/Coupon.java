package com.woowa.woowakit.domain.coupon.domain;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.woowa.woowakit.domain.model.BaseEntity;
import com.woowa.woowakit.domain.model.ExpiryDate;
import com.woowa.woowakit.domain.product.domain.Product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupons")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Coupon extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(name = "name")
	protected String name;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "expiry_date"))
	protected ExpiryDate expiryDate;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "minimum_order_amount"))
	protected MinimumOrderAmount minimumOrderAmount;

	@Embedded
	protected CouponTarget couponTarget;

	@Column(name = "member_id")
	protected Long memberId;

	public boolean isApplicable(final Product product) {
		return couponTarget.isApplicable(product);
	}

	public LocalDate getExpiryDate() {
		return expiryDate.getValue();
	}

	public int getMinimumOrderAmount() {
		return minimumOrderAmount.getValue();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Coupon)) {
			return false;
		}

		final Coupon coupon = (Coupon)o;
		return this.getId() != null && Objects.equals(getId(), coupon.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
