package com.woowa.woowakit.domain.order.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.woowa.woowakit.domain.order.dao.OrderRepositoryCustom;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

	@Query("select o from Order o join fetch o.orderItems where o.id = :id and o.memberId = :memberId")
	Optional<Order> findOrderById(@Param("id") final Long id, @Param("memberId") final Long memberId);

	Optional<Order> findByIdAndMemberId(final Long orderId, final Long memberId);
}
