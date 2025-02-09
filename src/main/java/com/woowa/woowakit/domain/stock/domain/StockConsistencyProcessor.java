package com.woowa.woowakit.domain.stock.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductSales;
import com.woowa.woowakit.domain.product.domain.ProductSalesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockConsistencyProcessor {

	private final StockRepository stockRepository;
	private final ProductSalesRepository productSalesRepository;

	@Transactional
	public void run(final Product product, final List<Stock> stocks) {
		Quantity difference = getDifference(product, stocks);
		saveProductSales(product.getId(), difference);
		matchStockConsistency(stocks, difference);
	}

	private Quantity getDifference(Product product, List<Stock> stocks) {
		return getTotalStockQuantity(stocks).subtract(Quantity.from(product.getQuantity()));
	}

	private void saveProductSales(final Long productId, final Quantity quantity) {
		try {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			productSalesRepository.save(ProductSales.builder()
				.productId(productId)
				.saleDate(yesterday)
				.sale(quantity.getValue())
				.build());
		} catch (Exception e) {
			log.warn("일일 판매량을 저장하는데 실패했습니다.", e);
		}
	}

	private void matchStockConsistency(final List<Stock> stocks, Quantity difference) {
		for (Stock stock : stocks) {
			Quantity removalQuantity = computeRemovalQuantity(difference, stock);
			difference = difference.subtract(removalQuantity);
			stock.subtractQuantity(removalQuantity);
			updateStockQuantity(difference, stock);
		}
		stockRepository.deleteStock(computeDeletingStock(stocks));
	}

	private void updateStockQuantity(final Quantity difference, final Stock stock) {
		if (difference.isEmpty()) {
			stockRepository.updateStockQuantity(stock.getId(), stock.getQuantity());
		}
	}

	private Quantity getTotalStockQuantity(final List<Stock> stocks) {
		return Quantity.from(stocks.stream()
			.mapToLong(stock -> stock.getQuantity().getValue())
			.sum());
	}

	private Quantity computeRemovalQuantity(final Quantity removingQuantity, final Stock stock) {
		return Quantity.from(Math.min(stock.getQuantity().getValue(), removingQuantity.getValue()));
	}

	private List<Long> computeDeletingStock(final List<Stock> stocks) {
		return stocks.stream()
			.filter(Stock::isEmpty)
			.map(Stock::getId)
			.collect(Collectors.toList());
	}
}
