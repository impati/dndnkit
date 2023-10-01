package com.woowa.woowakit.domain.product.api;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woowa.woowakit.domain.auth.annotation.Admin;
import com.woowa.woowakit.domain.product.application.ProductService;
import com.woowa.woowakit.domain.product.dto.request.AllProductSearchRequest;
import com.woowa.woowakit.domain.product.dto.request.InStockProductSearchRequest;
import com.woowa.woowakit.domain.product.dto.request.ProductCreateRequest;
import com.woowa.woowakit.domain.product.dto.request.ProductStatusUpdateRequest;
import com.woowa.woowakit.domain.product.dto.response.ProductDetailResponse;
import com.woowa.woowakit.domain.product.dto.response.ProductResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@Admin
	@PostMapping
	public ResponseEntity<Void> create(@Valid @RequestBody final ProductCreateRequest request) {
		final Long id = productService.create(request);

		return ResponseEntity.created(URI.create("/products/" + id)).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductDetailResponse> findById(@PathVariable final Long id) {
		final ProductDetailResponse response = productService.findById(id);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/rank")
	public ResponseEntity<List<ProductResponse>> getMainPage() {
		List<ProductResponse> response = productService.findRankingProducts();

		return ResponseEntity.ok(response);
	}

	@Admin
	@GetMapping
	public ResponseEntity<List<ProductResponse>> searchAllProducts(
		@Valid @ModelAttribute final AllProductSearchRequest request
	) {
		final List<ProductResponse> response = productService.searchAllProducts(request);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	public ResponseEntity<List<ProductResponse>> searchInStockProducts(
		@Valid @ModelAttribute final InStockProductSearchRequest request
	) {
		final List<ProductResponse> response = productService.searchInStockProducts(request);

		return ResponseEntity.ok(response);
	}

	@Admin
	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> updateStatus(
		@PathVariable final Long id,
		@Valid @RequestBody final ProductStatusUpdateRequest request
	) {
		productService.updateStatus(id, request);

		return ResponseEntity.noContent().build();
	}
}
