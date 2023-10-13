package com.woowa.woowakit.domain.stock.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.fixture.ProductFixture;
import com.woowa.woowakit.domain.model.ExpiryDate;
import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;
import com.woowa.woowakit.domain.stock.domain.Stock;
import com.woowa.woowakit.domain.stock.domain.StockRepository;
import com.woowa.woowakit.domain.stock.domain.StockType;

@SpringBootTest
class StockProcessingServiceTest {

	@Autowired
	private StockProcessingService stockProcessingService;

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
	@DisplayName("유통기한 테스트")
	void doProcessTestCaseOfExpiration() {
		// given
		Product product = productRepository.save(getProduct(75));

		List<Stock> stocks = List.of(
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 22), 10)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 22), 10)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 22), 10)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 22), 10)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 22), 20)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 29), 10)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 30), 10)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 30), 5))
		);

		// when
		stockProcessingService.doStockProcess(product.getId(), LocalDate.of(3023, 9, 16), stocks);

		// then
		assertThat(stockRepository.findAllByProductId(product.getId(), StockType.NORMAL))
			.hasSize(3)
			.extracting("quantity")
			.contains(Quantity.from(10), Quantity.from(10), Quantity.from(5));
		assertThat(productRepository.findById(product.getId()).get().getQuantity()).isEqualTo(25);
	}

	@Test
	@DisplayName("재고의 정합성 테스트")
	void doProcessTestCaseOfConsistency() {
		// given
		Product product = productRepository.save(getProduct(75));

		List<Stock> stocks = List.of(
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 22), 10)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 22), 10)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 22), 20)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 28), 30)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 28), 30)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 28), 30)),
			stockRepository.save(createStock(product, LocalDate.of(3023, 9, 28), 30))
		);

		// when
		stockProcessingService.doStockProcess(product.getId(), LocalDate.of(3023, 9, 16), stocks);

		// then
		Assertions.assertThat(stockRepository.findAllByProductId(product.getId(), StockType.NORMAL))
			.hasSize(3)
			.extracting(Stock::getQuantity)
			.contains(Quantity.from(15), Quantity.from(30), Quantity.from(30));
	}

	private Product getProduct(final long quantity) {
		return ProductFixture.getInStockProductBuilder()
			.quantity(quantity)
			.build();
	}

	private Stock createStock(Product product, LocalDate date, long quantity) {
		return Stock.builder()
			.expiryDate(ExpiryDate.from(date))
			.product(product)
			.quantity(Quantity.from(quantity))
			.build();
	}
}
