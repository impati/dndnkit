package com.woowa.woowakit.domain.stock.domain;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.model.ExpiryDate;
import com.woowa.woowakit.domain.product.domain.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpirationDateProcessor {

	private final StockRepository stockRepository;

	@Transactional
	public void run(final Product product, final LocalDate currentDate) {
		stockRepository.updateStatus(StockType.EXPIRED, ExpiryDate.from(currentDate.plusDays(6)), product.getId());
	}
}
