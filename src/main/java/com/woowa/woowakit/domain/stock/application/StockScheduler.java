package com.woowa.woowakit.domain.stock.application;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.woowa.woowakit.domain.product.domain.ProductRepository;
import com.woowa.woowakit.domain.stock.domain.Stock;
import com.woowa.woowakit.domain.stock.domain.StockRepository;
import com.woowa.woowakit.domain.stock.domain.StockType;
import com.woowa.woowakit.domain.stock.exception.StockBatchFailException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockScheduler {

	private final ProductRepository productRepository;
	private final StockRepository stockRepository;
	private final StockProcessingService stockProcessingService;

	@Scheduled(zone = "Asia/Seoul", cron = "0 0 0 * * ?")
	public void trigger() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		List<Long> productIds = productRepository.findAllIds();
		for (Long productId : productIds) {
			List<Stock> stocks = stockRepository.findAllByProductId(productId, StockType.NORMAL);
			executorService.submit(() -> doStockProcess(productId, stocks));
		}

		if (executorService.awaitTermination(60, TimeUnit.MINUTES)) {
			executorService.shutdown();
		}
	}

	private void doStockProcess(final Long productId, final List<Stock> stocks) {
		try {
			stockProcessingService.doStockProcess(productId, LocalDate.now(ZoneId.of("Asia/Seoul")), stocks);
		} catch (Exception e) {
			log.warn("productId = {} 배치 처리 실패", productId);
			throw new StockBatchFailException(e);
		}
	}
}
