package com.woowa.woowakit.domain.stock.application;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;
import com.woowa.woowakit.domain.product.exception.ProductNotExistException;
import com.woowa.woowakit.domain.stock.domain.ExpiryDate;
import com.woowa.woowakit.domain.stock.domain.Stock;
import com.woowa.woowakit.domain.stock.domain.StockRepository;
import com.woowa.woowakit.domain.stock.dto.request.StockCreateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

	private final StockRepository stockRepository;
	private final ProductRepository productRepository;

	@Transactional
	public long create(final StockCreateRequest request, final Long productId) {
		final Product product = findProductById(productId);
		final Stock stock = getStock(request.getExpiryDate(), product);

		stock.addQuantity(Quantity.from(request.getQuantity()));

		return stockRepository.save(stock).getId();
	}

	private Stock getStock(final LocalDate expiryDate, final Product product) {
		return stockRepository.findByProductIdAndExpiryDate(product.getId(), ExpiryDate.from(expiryDate))
			.orElse(getCreateStock(expiryDate, product));
	}

	private Stock getCreateStock(final LocalDate expiryDate, final Product product) {
		return Stock.of(expiryDate, product);
	}

	private Product findProductById(final Long id) {
		return productRepository.findByIdWithPessimistic(id)
			.orElseThrow(ProductNotExistException::new);
	}
}
