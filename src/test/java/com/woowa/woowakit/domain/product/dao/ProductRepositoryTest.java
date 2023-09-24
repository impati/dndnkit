package com.woowa.woowakit.domain.product.dao;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.woowa.woowakit.domain.fixture.ProductFixture;
import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.domain.ProductSalesRepository;
import com.woowa.woowakit.domain.product.domain.product.InStockProductSearchCondition;
import com.woowa.woowakit.domain.product.domain.product.Product;
import com.woowa.woowakit.domain.product.domain.product.ProductName;
import com.woowa.woowakit.domain.product.domain.product.ProductRepository;
import com.woowa.woowakit.domain.product.domain.product.ProductSales;
import com.woowa.woowakit.domain.product.domain.product.ProductSpecification;
import com.woowa.woowakit.domain.product.domain.product.ProductStatus;
import com.woowa.woowakit.global.config.JpaConfig;
import com.woowa.woowakit.global.config.QuerydslTestConfig;

@DisplayName("ProductRepository 단위 테스트")
@DataJpaTest
@Import({QuerydslTestConfig.class, JpaConfig.class})
class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductSalesRepository productSalesRepository;

	@Test
	@DisplayName("상품을 검색 조건에 따라 커서 기반으로 검색한다.(첫 페이지)")
	void search() {
		// given
		Product product1 = makeProduct("테스트1", 1500L, "testImg1");
		Product product2 = makeProduct("테스트2", 1500L, "testImg2");
		Product product3 = makeProduct("테스트3", 1500L, "testImg3");
		Product product4 = makeProduct("테스트11", 1500L, "testImg4");
		Product product5 = makeProduct("테스트12", 1500L, "testImg5");

		// when
		InStockProductSearchCondition inStockProductSearchCondition = InStockProductSearchCondition.of("1", null, null,
			5, LocalDate.now());
		List<ProductSpecification> result = productRepository.searchInStockProducts(inStockProductSearchCondition);

		// then
		Assertions.assertThat(result).hasSize(3);
		Assertions.assertThat(result.get(0))
			.extracting(ProductSpecification::getProduct)
			.extracting(Product::getId)
			.isEqualTo(product1.getId());
		Assertions.assertThat(result.get(1))
			.extracting(ProductSpecification::getProduct)
			.extracting(Product::getId)
			.isEqualTo(product4.getId());
		Assertions.assertThat(result.get(2))
			.extracting(ProductSpecification::getProduct)
			.extracting(Product::getId)
			.isEqualTo(product5.getId());
	}

	@Test
	@DisplayName("상품을 검색 조건에 따라 커서 기반으로 검색한다.(두 번째 페이지)")
	void searchCursor() {
		// given
		Product product1 = makeProduct("테스트1", 1500L, "testImg1");
		Product product2 = makeProduct("테스트2", 1500L, "testImg2");
		Product product3 = makeProduct("테스트3", 1500L, "testImg3");
		Product product4 = makeProduct("테스트4", 1500L, "testImg4");
		Product product5 = productRepository.save(Product.of("테스트5", 1500L, "testImg5"));
		Product product6 = productRepository.save(Product.of("테스트5", 1500L, "testImg5"));
		Product product7 = productRepository.save(Product.of("테스트5", 1500L, "testImg5"));

		// when
		InStockProductSearchCondition inStockProductSearchCondition = InStockProductSearchCondition.of("테스트",
			product2.getId(), null, 4,
			null);
		List<ProductSpecification> result = productRepository.searchInStockProducts(inStockProductSearchCondition);

		// then
		Assertions.assertThat(result).hasSize(2);
		Assertions.assertThat(result.get(0))
			.extracting(ProductSpecification::getProduct)
			.extracting(Product::getId)
			.isEqualTo(product3.getId());
		Assertions.assertThat(result.get(1))
			.extracting(ProductSpecification::getProduct)
			.extracting(Product::getId)
			.isEqualTo(product4.getId());
	}

	private Product makeProduct(String name, Long price, String image) {
		Product product = Product.of(name, price, image);
		product.addQuantity(Quantity.from(10L));
		product.updateProductStatus(ProductStatus.IN_STOCK);
		productRepository.save(product);
		return product;
	}

	@Test
	@DisplayName("상품 판매량 순 조회")
	void searchProductsTest() {
		Product productA = productRepository.save(getProduct("productA"));
		Product productB = productRepository.save(getProduct("productB"));
		Product productC = productRepository.save(getProduct("productC"));
		Product productD = productRepository.save(getProduct("productD"));
		Product productE = productRepository.save(getProduct("productE"));
		Product productF = productRepository.save(getProduct("productF"));
		Product productG = productRepository.save(getProduct("productG"));
		Product productH = productRepository.save(getProduct("productH"));
		Product productI = productRepository.save(getProduct("productI"));
		Product productJ = productRepository.save(getProduct("productJ"));

		productSalesRepository.save(createProductSale(productA, 50, LocalDate.of(2023, 8, 25)));
		productSalesRepository.save(createProductSale(productB, 60, LocalDate.of(2023, 8, 25)));
		productSalesRepository.save(createProductSale(productC, 30, LocalDate.of(2023, 8, 25)));
		productSalesRepository.save(createProductSale(productD, 30, LocalDate.of(2023, 8, 25)));
		productSalesRepository.save(createProductSale(productE, 30, LocalDate.of(2023, 8, 25)));
		productSalesRepository.save(createProductSale(productF, 30, LocalDate.of(2023, 8, 25)));
		productSalesRepository.save(createProductSale(productG, 30, LocalDate.of(2023, 8, 25)));
		productSalesRepository.save(createProductSale(productH, 30, LocalDate.of(2023, 8, 25)));
		productSalesRepository.save(createProductSale(productI, 30, LocalDate.of(2023, 8, 25)));
		productSalesRepository.save(createProductSale(productJ, 30, LocalDate.of(2023, 8, 25)));

		InStockProductSearchCondition condition = InStockProductSearchCondition.of(null, productD.getId(), 30L, 4,
			LocalDate.of(2023, 8, 25));
		List<ProductSpecification> products = productRepository.searchInStockProducts(condition);
		Assertions.assertThat(products).hasSize(4)
			.extracting(ProductSpecification::getProduct)
			.extracting(Product::getName)
			.contains(
				ProductName.from("productE"),
				ProductName.from("productF"),
				ProductName.from("productG"),
				ProductName.from("productH"));
	}

	private Product getProduct(final String productName) {
		return ProductFixture.getProductBuilder()
			.name(ProductName.from(productName))
			.build();
	}

	private ProductSales createProductSale(final Product productA, final long quantity, final LocalDate saleDate) {
		return ProductSales.builder()
			.productId(productA.getId())
			.sale(Quantity.from(quantity))
			.saleDate(saleDate)
			.build();
	}
}
