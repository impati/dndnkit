package com.woowa.woowakit.domain.order.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.woowa.woowakit.domain.auth.domain.Email;
import com.woowa.woowakit.domain.auth.domain.EncodedPassword;
import com.woowa.woowakit.domain.auth.domain.Member;
import com.woowa.woowakit.domain.auth.domain.Role;
import com.woowa.woowakit.domain.auth.infra.PBKDF2PasswordEncoder;
import com.woowa.woowakit.domain.fixture.OrderFixture;
import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderItem;
import com.woowa.woowakit.domain.order.domain.OrderRepository;
import com.woowa.woowakit.global.config.JpaConfig;
import com.woowa.woowakit.global.config.QuerydslTestConfig;

@DisplayName("OrderRepository 단위 테스트")
@DataJpaTest
@Import({QuerydslTestConfig.class, JpaConfig.class})
class OrderRepositoryTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private OrderRepository orderRepository;

	@Test
	@DisplayName("회원의 주문 상세 정보를 확인한다.")
	void findById() {
		// given
		final Member member = createMember();
		entityManager.persist(member);

		OrderItem orderItem1 = OrderFixture.getOrderItemBuilder()
			.productId(1L)
			.build();

		OrderItem orderItem2 = OrderFixture.getOrderItemBuilder()
			.productId(2L)
			.build();

		Order order = OrderFixture.getOrderBuilder()
			.memberId(member.getId())
			.orderItems(List.of(orderItem1, orderItem2))
			.build();

		Long orderId = orderRepository.save(order).getId();

		// when
		Order result = orderRepository.findOrderById(orderId, member.getId()).get();

		// then
		assertThat(result).extracting(Order::getId).isEqualTo(orderId);
		assertThat(result.getOrderItems()).hasSize(2);
		assertThat(result.getOrderItems().get(0)).extracting("id").isEqualTo(orderItem1.getId());
		assertThat(result.getOrderItems().get(1)).extracting("id").isEqualTo(orderItem2.getId());
	}

	@Test
	@DisplayName("회원의 주문 정보들을 확인한다.")
	void findAllByMemberId() {
		// given
		Member member = createMember();
		entityManager.persist(member);

		OrderItem orderItem1 = OrderFixture.getOrderItemBuilder()
			.productId(1L)
			.build();

		OrderItem orderItem2 = OrderFixture.getOrderItemBuilder()
			.productId(2L)
			.build();

		Order order1 = OrderFixture.getOrderBuilder()
			.memberId(member.getId())
			.orderItems(List.of(orderItem1))
			.build();
		order1.pay();

		Order order2 = OrderFixture.getOrderBuilder()
			.memberId(member.getId())
			.orderItems(List.of(orderItem2))
			.build();
		order2.pay();

		orderRepository.save(order1);
		orderRepository.save(order2);

		// when
		List<Order> result = orderRepository.findOrdersByMemberId(member.getId(), order2.getId() + 1L, 20);

		// then
		assertThat(result).hasSize(2);
	}

	private Member createMember() {
		return Member.builder()
			.role(Role.USER)
			.email(Email.from("tamtam@hello.com"))
			.encodedPassword(EncodedPassword.of("happytamtam", new PBKDF2PasswordEncoder()))
			.name("탐탐")
			.build();
	}
}
