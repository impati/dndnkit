package com.woowa.woowakit.domain.stock.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

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
import com.woowa.woowakit.domain.product.domain.ProductSales;
import com.woowa.woowakit.domain.product.domain.ProductSalesRepository;

@SpringBootTest
class StockConsistencyProcessorTest {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockConsistencyProcessor stockConsistencyProcessor;

	@Autowired
	private ProductSalesRepository productSalesRepository;

	@AfterEach
	void setup() {
		productRepository.deleteAll();
		stockRepository.deleteAll();
		productSalesRepository.deleteAll();
	}

	@Test
	@DisplayName("상품 수량과 재고 테이블의 정합성을 맞춘다.")
	void doProcessTest() {
		// given
		Product product = productRepository.save(getProduct(35));

		Stock stockA = stockRepository.save(createStock(product, LocalDate.of(3023, 9, 22), 10));
		Stock stockB = stockRepository.save(createStock(product, LocalDate.of(3023, 9, 25), 20));
		Stock stockC = stockRepository.save(createStock(product, LocalDate.of(3023, 9, 28), 30));

		// when
		stockConsistencyProcessor.run(product, List.of(stockA, stockB, stockC));

		// then
		List<Stock> stocks = stockRepository.findAllByProductId(product.getId(), StockType.NORMAL);
		assertThat(stocks).hasSize(2)
			.extracting(Stock::getQuantity)
			.contains(Quantity.from(5), Quantity.from(30));

		List<ProductSales> productSales = productSalesRepository.findByProductId(product.getId());
		assertThat(productSales).hasSize(1)
			.extracting(ProductSales::getSale)
			.contains(25L);
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
