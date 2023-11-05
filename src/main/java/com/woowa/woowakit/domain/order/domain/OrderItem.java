package com.woowa.woowakit.domain.order.domain;

import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.woowa.woowakit.domain.model.BaseEntity;
import com.woowa.woowakit.domain.model.Image;
import com.woowa.woowakit.domain.model.Money;
import com.woowa.woowakit.domain.model.Quantity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "order_items")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	private Long id;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "coupon_id")
	private Long couponId;

	@Column(name = "name")
	private String name;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "image"))
	private Image image;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "price"))
	private Money price;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "quantity"))
	private Quantity quantity;

	@Builder
	private OrderItem(
		final Long productId,
		final String name,
		final String image,
		final long price,
		final long quantity
	) {
		this.productId = productId;
		this.name = name;
		this.image = Image.from(image);
		this.price = Money.from(price);
		this.quantity = Quantity.from(quantity);
	}

	public void applyCoupon(final Long couponId) {
		this.couponId = couponId;
	}

	Money calculateTotalPrice() {
		return price.multiply(quantity.getValue());
	}

	public long getPrice() {
		return price.getValue();
	}

	public String getImage() {
		return image.getValue();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OrderItem)) {
			return false;
		}

		final OrderItem orderItem = (OrderItem)o;
		return this.getId() != null && Objects.equals(getId(), orderItem.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
