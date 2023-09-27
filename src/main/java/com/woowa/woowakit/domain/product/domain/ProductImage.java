package com.woowa.woowakit.domain.product.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.Embeddable;

import com.woowa.woowakit.domain.product.exception.ProductImageException;

import lombok.Getter;

@Embeddable
@Getter
public class ProductImage {

	private static final String IMAGE_REGEX = "^(https?|ftp):\\/\\/[^\\s/$.?#].[^\\s]*$";
	private String value;

	protected ProductImage() {
	}

	private ProductImage(final String value) {
		validate(value);
		this.value = value;
	}

	private void validate(final String value) {
		if (!Pattern.matches(IMAGE_REGEX, value)) {
			throw new ProductImageException();
		}
	}

	public static ProductImage from(final String value) {
		return new ProductImage(value);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ProductImage)) {
			return false;
		}

		final ProductImage that = (ProductImage)o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
