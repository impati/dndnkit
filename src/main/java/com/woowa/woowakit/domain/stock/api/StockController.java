package com.woowa.woowakit.domain.stock.api;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woowa.woowakit.domain.auth.annotation.Admin;
import com.woowa.woowakit.domain.stock.application.StockService;
import com.woowa.woowakit.domain.stock.dto.request.StockCreateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class StockController {

	private final StockService stockService;

	@Admin
	@PostMapping("/{productId}")
	public ResponseEntity<Void> addStock(
		@PathVariable final Long productId,
		@Valid @RequestBody final StockCreateRequest request
	) {
		long resultId = stockService.create(request, productId);

		return ResponseEntity.created(URI.create("/products/" + productId + "/stocks/" + resultId)).build();
	}
}
