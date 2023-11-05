package com.woowa.woowakit.domain.order.application;

import static com.woowa.woowakit.domain.fixture.CouponFixture.*;
import static com.woowa.woowakit.domain.fixture.ProductFixture.*;
import static com.woowa.woowakit.domain.member.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.woowa.woowakit.domain.auth.domain.AuthPrincipal;
import com.woowa.woowakit.domain.auth.domain.Member;
import com.woowa.woowakit.domain.auth.domain.MemberRepository;
import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponFrame;
import com.woowa.woowakit.domain.coupon.domain.CouponFrameRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;
import com.woowa.woowakit.domain.coupon.dto.response.CouponResponse;
import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderRepository;
import com.woowa.woowakit.domain.order.domain.OrderStatus;
import com.woowa.woowakit.domain.order.domain.PaymentClient;
import com.woowa.woowakit.domain.order.dto.request.CouponAppliedOrderItemRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderCreateRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderPayRequest;
import com.woowa.woowakit.domain.order.dto.response.OrderItemResponse;
import com.woowa.woowakit.domain.order.dto.response.OrderResponse;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductBrand;
import com.woowa.woowakit.domain.product.domain.ProductCategory;
import com.woowa.woowakit.domain.product.domain.ProductRepository;

import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("OrderCommandService 테스트")
class OrderCommandServiceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderCommandService orderCommandService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CouponFrameRepository couponFrameRepository;

	@Autowired
	private CouponRepository couponRepository;

	@MockBean
	private PaymentClient paymentClient;

	@AfterEach
	void tearDown() {
		memberRepository.deleteAll();
		productRepository.deleteAll();
		orderRepository.deleteAll();
		couponFrameRepository.deleteAll();
		couponRepository.deleteAll();
	}

	@Test
	@DisplayName("가주문 생성 후 응답 시 orderItem 마다 적용 가능한 쿠폰 정보를 응답한다.")
	void createOrder() {
		Member member = saveMember();
		Product koreaProduct = saveProduct(ProductBrand.COOKIT, ProductCategory.KOREAN);
		Product chineseProduct = saveProduct(ProductBrand.MOKRAN, ProductCategory.CHINESE);
		LocalDate now = LocalDate.of(3023, 10, 22);
		Coupon mokranBrandCoupon = saveCoupon(member, CouponTarget.from(ProductBrand.MOKRAN), now);
		Coupon cookitBrandCoupon = saveCoupon(member, CouponTarget.from(ProductBrand.COOKIT), now);
		Coupon chineseBrandCoupon = saveCoupon(member, CouponTarget.from(ProductCategory.CHINESE), now);
		List<OrderCreateRequest> request = List.of(
			OrderCreateRequest.of(koreaProduct.getId(), 1L),
			OrderCreateRequest.of(chineseProduct.getId(), 1L)
		);

		OrderResponse orderResponse = orderCommandService.create(AuthPrincipal.from(member), request, now);

		List<OrderItemResponse> orderItemResponses = orderResponse.getOrderItems();
		assertThat(orderItemResponses).hasSize(2);
		assertThat(orderItemResponses.stream()
			.filter(orderItemResponse -> orderItemResponse.getProductId().equals(koreaProduct.getId()))
			.findAny()
			.orElseThrow()
			.getCouponResponses()
			.getCouponResponses())
			.hasSize(1)
			.extracting(CouponResponse::getCouponId)
			.contains(cookitBrandCoupon.getId());
		assertThat(orderItemResponses.stream()
			.filter(orderItemResponse -> orderItemResponse.getProductId().equals(chineseProduct.getId()))
			.findAny()
			.orElseThrow()
			.getCouponResponses()
			.getCouponResponses())
			.hasSize(2)
			.extracting(CouponResponse::getCouponId)
			.containsAnyOf(mokranBrandCoupon.getId(), chineseBrandCoupon.getId());
	}

	@Test
	@DisplayName("가주문 이후 결제할 때 주문 상품에 대한 쿠폰 적용 정보를 반영한다.")
	void pay() {
		Member member = saveMember();
		Product chineseProduct = saveProduct(ProductBrand.MOKRAN, ProductCategory.CHINESE);
		LocalDate now = LocalDate.of(3023, 10, 22);
		Coupon chineseBrandCoupon = saveCoupon(member, CouponTarget.from(ProductCategory.CHINESE), now);
		List<OrderCreateRequest> request = List.of(
			OrderCreateRequest.of(chineseProduct.getId(), 2L)
		);
		OrderResponse orderResponse = orderCommandService.create(AuthPrincipal.from(member), request, now);
		Long orderItemId = orderResponse.getOrderItems().get(0).getId();
		when(paymentClient.validatePayment(any(), any(), any())).thenReturn(Mono.empty());

		orderCommandService.pay(
			AuthPrincipal.from(member),
			orderResponse.getId(),
			OrderPayRequest.of(
				"paymentKey",
				List.of(CouponAppliedOrderItemRequest.of(orderItemId, chineseBrandCoupon.getId())))
		);

		Order order = orderRepository.findById(orderResponse.getId()).orElseThrow();
		assertThat(order)
			.extracting(Order::getOrderStatus, Order::getTotalPrice)
			.contains(OrderStatus.PAYED, 2 * chineseProduct.getPrice() - chineseBrandCoupon.getDiscount());
	}

	private Product saveProduct(final ProductBrand productBrand, final ProductCategory productCategory) {
		return productRepository.save(getInStockProductBuilder()
			.productBrand(productBrand)
			.productCategories(List.of(productCategory))
			.build());
	}

	private Member saveMember() {
		return memberRepository.save(anMember().build());
	}

	private Coupon saveCoupon(
		final Member member,
		final CouponTarget couponTarget,
		final LocalDate now
	) {
		CouponFrame couponFrame = saveCouponFrame(couponTarget);

		return couponRepository.save(couponFrame.makeCoupon(member.getId(), now));
	}

	private CouponFrame saveCouponFrame(final CouponTarget couponTarget) {
		return couponFrameRepository.save(getDefaultCouponFrameBuilder()
			.couponType(CouponType.FIXED)
			.minimumOrderAmount(1000)
			.discount(1000)
			.couponTarget(couponTarget)
			.build());
	}
}
