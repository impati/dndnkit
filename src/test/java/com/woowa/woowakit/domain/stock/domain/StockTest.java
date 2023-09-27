package com.woowa.woowakit.domain.stock.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.fixture.ProductFixture;
import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.domain.Product;

@DisplayName("Stock 도메인 단위 테스트")
class StockTest {

	@Test
	@DisplayName("재고를 추가하면 상품 재고 수량에도 반영된다.")
	void addQuantity() {
		// given
		Product product = getProduct();

		Stock stock = Stock.of(LocalDate.now().plusDays(1), product);

		// when
		stock.addQuantity(Quantity.from(5));

		// then
		assertThat(product)
			.extracting(Product::getQuantity)
			.isEqualTo(5L);
	}

	@Test
	@DisplayName("재고 수량을 빼면 재고 수량이 감소한다.")
	void subtractQuantity() {
		// given
		Product product = getProduct();
		;
		Stock stock = Stock.of(LocalDate.now().plusDays(1), product);
		stock.addQuantity(Quantity.from(5));

		// when
		stock.subtractQuantity(Quantity.from(3));

		// then
		assertThat(stock.getQuantity()).isEqualTo(Quantity.from(2));
	}

	@Test
	@DisplayName("재고 수량이 0이면 isEmpty 가 true 이다.")
	void isEmpty() {
		// given
		Product product = getProduct();
		;
		Stock stock = Stock.of(LocalDate.now().plusDays(1), product);
		stock.addQuantity(Quantity.from(5));
		stock.subtractQuantity(Quantity.from(5));

		// when , then
		assertThat(stock.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("재고 유통기한이 만료되면 상태가 EXPIRED 가 된다.")
	void CheckExpiredExpiryDateCaseOfExpired() {
		// given
		Product product = getProduct();
		;
		Stock stock = Stock.of(LocalDate.now().plusDays(6), product);
		stock.addQuantity(Quantity.from(5));

		// when
		stock.CheckExpiredExpiryDate(LocalDate.now());

		// then
		assertThat(stock.getStockType()).isEqualTo(StockType.EXPIRED);
	}

	@Test
	@DisplayName("재고 유통기한이 만료가 안되면 EXPIRED 가 된다.")
	void CheckExpiredExpiryDateCaseOfNormal() {
		Product product = getProduct();
		Stock stock = Stock.of(LocalDate.now().plusDays(7), product);
		stock.addQuantity(Quantity.from(5));

		// when
		stock.CheckExpiredExpiryDate(LocalDate.now());

		// then
		assertThat(stock.getStockType()).isEqualTo(StockType.NORMAL);
	}

	private Product getProduct() {
		return ProductFixture.getInStockProductBuilder()
			.quantity(0)
			.build();
	}
}
