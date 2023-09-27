package com.woowa.woowakit.domain.stock.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.woowa.woowakit.domain.fixture.ProductFixture;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;
import com.woowa.woowakit.global.config.JpaConfig;
import com.woowa.woowakit.global.config.QuerydslTestConfig;

@DataJpaTest
@Import({QuerydslTestConfig.class, JpaConfig.class})
class StockRepositoryTest {

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private ProductRepository productRepository;

	@Test
	void findByProductIdAndExpiryDate() {
		// given
		LocalDate expiryDate = LocalDate.of(2223, 12, 31);
		Product product = productRepository.save(getProduct());
		Stock stock = stockRepository.save(Stock.of(expiryDate, product));

		// when
		Stock result = stockRepository.findByProductIdAndExpiryDate(product.getId(), ExpiryDate.from(expiryDate)).get();

		// then
		assertThat(result).isSameAs(stock);
	}

	private static Product getProduct() {
		return ProductFixture.getProductBuilder()
			.build();
	}
}
