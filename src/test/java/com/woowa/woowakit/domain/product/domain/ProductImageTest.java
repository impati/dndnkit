package com.woowa.woowakit.domain.product.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.woowa.woowakit.domain.product.exception.ProductImageException;

class ProductImageTest {

	@ParameterizedTest
	@ValueSource(strings = {"http://imagePath", "https://imagePath/path", "https://imagePath?key=value"})
	@DisplayName("상품 이미지는 URL 형식이어야한다. ")
	void ProductImage(final String value) {
		ProductImage productImage = ProductImage.from(value);

		assertThat(productImage.getValue()).isEqualTo(value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"http:/imagePath.org", "https//imagePath", "https2://imagePath.org"})
	@DisplayName("상품 이미지는 URL 형식이어야한다. ")
	void ProductImageFail(final String value) {
		assertThatCode(() -> ProductImage.from(value))
			.isInstanceOf(ProductImageException.class)
			.hasMessage("상품 이미지 형식이 올바르지 않습니다.");
	}
}
