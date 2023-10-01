package com.woowa.woowakit.domain.cart.domain;

import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.woowa.woowakit.domain.cart.exception.CartItemQuantityException;
import com.woowa.woowakit.domain.cart.exception.InvalidProductInCartItemException;
import com.woowa.woowakit.domain.model.BaseEntity;
import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.domain.Product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cart_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id")
	private Long memberId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "quantity"))
	private Quantity quantity;

	@Builder
	private CartItem(
		final Long memberId,
		final Product product,
		final long quantity
	) {
		this.memberId = memberId;
		this.product = product;
		this.quantity = Quantity.from(quantity);
	}

	public void addQuantity(final long quantity) {
		validateCartItemWithProduct(this.quantity.getValue() + quantity);
		this.quantity = this.quantity.add(quantity);
	}

	public void updateQuantity(final long quantity) {
		validateCartItemWithProduct(quantity);
		this.quantity = Quantity.from(quantity);
	}

	private void validateCartItemWithProduct(final long requiredQuantity) {
		if (!product.isOnSale()) {
			throw new InvalidProductInCartItemException();
		}

		if (!product.isEnoughQuantity(Quantity.from(requiredQuantity))) {
			throw new CartItemQuantityException();
		}
	}

	public boolean isMyCartItem(final Long memberId) {
		return Objects.equals(this.memberId, memberId);
	}

	public long getQuantity() {
		return quantity.getValue();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CartItem)) {
			return false;
		}

		final CartItem cartItem = (CartItem)o;
		return this.getId() != null && Objects.equals(getId(), cartItem.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
