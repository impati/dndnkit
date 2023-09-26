package com.woowa.woowakit.domain.payment.domain;

import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.woowa.woowakit.domain.model.BaseEntity;
import com.woowa.woowakit.domain.model.Money;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "payments")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "payment_key")
	private String paymentKey;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "total_price"))
	private Money totalPrice;

	@Builder
	private Payment(
		final String paymentKey,
		final long totalPrice,
		final Long orderId
	) {
		this.paymentKey = paymentKey;
		this.totalPrice = Money.from(totalPrice);
		this.orderId = orderId;
	}

	public long getTotalPrice() {
		return totalPrice.getValue();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Payment)) {
			return false;
		}

		final Payment payment = (Payment)o;
		return this.getId() != null && Objects.equals(getId(), payment.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
