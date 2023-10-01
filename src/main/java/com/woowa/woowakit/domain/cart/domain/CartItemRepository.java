package com.woowa.woowakit.domain.cart.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.woowa.woowakit.domain.cart.dao.CartItemCustomRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemCustomRepository {

	Optional<CartItem> findCartItemByMemberIdAndProductId(final Long memberId, final Long productId);

	@Modifying
	@Query("delete from CartItem c where c.product.id in :productIds and c.memberId = :memberId")
	void deleteAllByProductIdAndMemberId(
		@Param("memberId") final Long memberId,
		@Param("productIds") final List<Long> productIds
	);

	@Modifying
	@Query("delete from CartItem c where c.id in :cartItemIds and c.memberId = :memberId")
	void deleteCartItems(@Param("memberId") final Long memberId, @Param("cartItemIds") final List<Long> cartItemIds);
}
