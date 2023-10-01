package com.woowa.woowakit.domain.stock.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.cart.exception.ProductNotExistException;
import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;
import com.woowa.woowakit.domain.stock.domain.ExpirationDateProcessor;
import com.woowa.woowakit.domain.stock.domain.Stock;
import com.woowa.woowakit.domain.stock.domain.StockConsistencyProcessor;
import com.woowa.woowakit.domain.stock.domain.StockRepository;
import com.woowa.woowakit.domain.stock.domain.StockType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockProcessingService {

	private static final int DAYS = 6;
	private static final long DEFAULT_SUBTRACT = 0L;
	private final ProductRepository productRepository;
	private final StockConsistencyProcessor stockConsistencyProcessor;
	private final ExpirationDateProcessor expirationDateProcessor;
	private final StockRepository stockRepository;

	@Transactional
	public void doStockProcess(
		final Long productId,
		final LocalDate currentDate,
		final List<Stock> stocks
	) {
		Product product = getProductById(productId);
		doStockConsistencyProcess(product, stocks);
		doExpirationDateProcess(product, currentDate);
	}

	private Product getProductById(final Long productId) {
		return productRepository.findByIdWithPessimistic(productId)
			.orElseThrow(ProductNotExistException::new);
	}

	private void doStockConsistencyProcess(final Product product, final List<Stock> stocks) {
		stockConsistencyProcessor.run(product, stocks);
	}

	private void doExpirationDateProcess(final Product product, final LocalDate currentDate) {
		expirationDateProcessor.run(product, currentDate);
		subtractProductQuantity(product, currentDate);
	}

	private void subtractProductQuantity(final Product product, final LocalDate currentDate) {
		long subtractExpiryQuantity = stockRepository.countStockByExpiry(
			product.getId(),
			StockType.EXPIRED.name(),
			currentDate.plusDays(DAYS)
		).orElse(DEFAULT_SUBTRACT);

		if (!isPossibleSubtractExpiryQuantity(product, subtractExpiryQuantity)) {
			product.subtractQuantity(Quantity.from(subtractExpiryQuantity));
		}
	}

	private boolean isPossibleSubtractExpiryQuantity(final Product product, final long subtractExpiryQuantity) {
		return product.isSmallerThan(subtractExpiryQuantity);
	}
}
