package com.woowa.woowakit.domain.product.dto.request;

import javax.validation.constraints.NotNull;

import com.woowa.woowakit.domain.product.domain.ProductStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProductStatusUpdateRequest {

	@NotNull(message = "상품 상태는 필수입니다.")
	private ProductStatus productStatus;

	public static ProductStatusUpdateRequest of(final ProductStatus productStatus) {
		return new ProductStatusUpdateRequest(productStatus);
	}
}
