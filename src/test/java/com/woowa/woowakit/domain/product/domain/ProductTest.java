package com.woowa.woowakit.domain.product.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.woowa.woowakit.domain.fixture.ProductFixture;
import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.exception.UpdateProductStatusFailException;

@DisplayName("Product 도메인 엔티티 테스트")
class ProductTest {

	@Test
	@DisplayName("Product 생성 시 가등록 상태로 생성된다.")
	void createProductWithPreRegistrationStatus() {
		Product product = getProduct();

		assertThat(product).extracting("status").isEqualTo(ProductStatus.PRE_REGISTRATION);
	}

	@Test
	@DisplayName("Product 생성 시 수량이 0인 상태로 생성된다.")
	void createProductWithZeroQuantity() {
		Product product = getProduct();

		assertThat(product.getQuantity()).isZero();
	}

	@Test
	@DisplayName("Product 가 가진 수량보다 많은 수량을 요구할 경우 false 를 반환한다.")
	void isEnoughQuantityFalse() {
		Product product = getInStockProduct(100);

		assertThat(product.isEnoughQuantity(Quantity.from(101))).isFalse();
	}

	@Test
	@DisplayName("Product 가 가진 수량보다 적거나 같은 수량을 요구할 경우 true 를 반환한다.")
	void isEnoughQuantityTrue() {
		Product product = getInStockProduct(100);

		assertThat(product.isEnoughQuantity(Quantity.from(99))).isTrue();
	}

	@Test
	@DisplayName("Product 가 IN_STOCK 이라면 구매가능한 상태이므로 true 를 반환한다.")
	void isAvailablePurchaseTest() {
		Product product = getInStockProduct(100);

		assertThat(product.isOnSale()).isTrue();
	}

	@ParameterizedTest
	@EnumSource(value = ProductStatus.class, names = {"IN_STOCK"}, mode = EnumSource.Mode.EXCLUDE)
	@DisplayName("Product 가 IN_STOCK 가 아니라면 구매가능한 상태이므로 false 를 반환한다.")
	void isNotAvailablePurchaseTest(final ProductStatus productStatus) {
		Product product = getInStockProduct(productStatus);

		assertThat(product.isOnSale()).isFalse();
	}

	@Test
	@DisplayName("Product 상태를 변경할 수 있다.")
	void updateProductStatus() {
		long quantity = 10;
		Product product = getProduct(quantity);

		product.updateProductStatus(ProductStatus.IN_STOCK);

		assertThat(product.getStatus()).isEqualTo(ProductStatus.IN_STOCK);
	}

	@Test
	@DisplayName("재고가 0이라면 Product 상태를 판매 중 상태로 변경할 수 없다")
	void updateProductStatusFail() {
		Product product = getProduct(0);

		assertThatCode(() -> product.updateProductStatus(ProductStatus.IN_STOCK))
			.isInstanceOf(UpdateProductStatusFailException.class)
			.hasMessage("재고가 0인 상태는 판매 중 상태로 변경할 수 없습니다.");
	}

	@Test
	@DisplayName("재고가 0이하라면 Product 상태가 품절이된다.")
	void subtractQuantity() {
		Product product = getInStockProduct(10);

		product.subtractQuantity(Quantity.from(10));

		assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
	}

	private Product getInStockProduct(final ProductStatus status) {
		return ProductFixture.getInStockProductBuilder()
			.status(status)
			.build();
	}

	private Product getInStockProduct(final long quantity) {
		return ProductFixture.getInStockProductBuilder()
			.quantity(quantity)
			.build();
	}

	private Product getProduct() {
		return Product.builder()
			.imageUrl("https://impagePath")
			.build();
	}

	private Product getProduct(final long quantity) {
		return Product.builder()
			.quantity(quantity)
			.imageUrl("https://impagePath")
			.build();
	}
}

