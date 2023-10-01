package com.woowa.woowakit.domain.cart.dao;

import static com.querydsl.core.types.Projections.*;
import static com.woowa.woowakit.domain.cart.domain.QCartItem.*;
import static com.woowa.woowakit.domain.product.domain.QProduct.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woowa.woowakit.domain.cart.domain.CartItemSpecification;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CartItemCustomRepositoryImpl implements CartItemCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<CartItemSpecification> findCartItemByMemberId(final Long memberId) {
		return jpaQueryFactory
			.select(
				constructor(
					CartItemSpecification.class,
					cartItem.quantity,
					product.price,
					product.name,
					product.imageUrl,
					product.id,
					cartItem.id))
			.from(cartItem)
			.join(cartItem.product, product)
			.where(cartItem.memberId.eq(memberId))
			.fetch();
	}

	@Override
	public Optional<CartItemSpecification> findCartItemByIdAndMemberId(final Long memberId, final Long id) {
		return jpaQueryFactory
			.select(
				constructor(
					CartItemSpecification.class,
					cartItem.quantity,
					product.price,
					product.name,
					product.imageUrl,
					product.id,
					cartItem.id))
			.from(cartItem)
			.join(cartItem.product, product)
			.where(cartItem.memberId.eq(memberId), cartItem.id.eq(id))
			.stream()
			.findAny();
	}
}
