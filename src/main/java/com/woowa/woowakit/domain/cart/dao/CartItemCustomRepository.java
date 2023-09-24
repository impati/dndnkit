package com.woowa.woowakit.domain.cart.dao;

import java.util.List;
import java.util.Optional;

import com.woowa.woowakit.domain.cart.domain.CartItemSpecification;

public interface CartItemCustomRepository {

	List<CartItemSpecification> findCartItemByMemberId(final Long memberId);

	Optional<CartItemSpecification> findCartItemByIdAndMemberId(final Long memberId, final Long id);
}
