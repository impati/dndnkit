package com.woowa.woowakit.domain.stock.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.fixture.ProductFixture;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;

@SpringBootTest
class ExpirationDateProcessorTest {

	@Autowired
	private ExpirationDateProcessor expirationDateProcessor;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StockRepository stockRepository;

	@AfterEach
	void setup() {
		productRepository.deleteAll();
		stockRepository.deleteAll();
	}

	@Test
	@DisplayName("재고 중 정책에 따라 유통기한이 만료된 재고는 EXPIRED 처리한다.")
	void doProcess() {
		// given
		Product product = productRepository.save(getProduct());

		stockRepository.save((Stock.of(LocalDate.of(3023, 9, 11), product)));
		stockRepository.save((Stock.of(LocalDate.of(3023, 9, 12), product)));
		stockRepository.save((Stock.of(LocalDate.of(3023, 9, 13), product)));
		stockRepository.save((Stock.of(LocalDate.of(3023, 9, 14), product)));

		// when
		expirationDateProcessor.run(product, LocalDate.of(3023, 9, 5));

		// then
		assertThat(stockRepository.findAllByProductId(product.getId(), StockType.EXPIRED))
			.hasSize(1);
	}

	private Product getProduct() {
		return ProductFixture.getInStockProductBuilder()
			.price(10000L)
			.quantity(100)
			.name("된장 밀키트")
			.build();
	}
}
