package com.woowa.woowakit.domain.product.dao;

import static com.woowa.woowakit.domain.order.domain.QOrder.*;
import static com.woowa.woowakit.domain.product.domain.QProduct.*;
import static com.woowa.woowakit.domain.product.domain.QProductSales.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woowa.woowakit.domain.product.domain.AllProductSearchCondition;
import com.woowa.woowakit.domain.product.domain.InStockProductSearchCondition;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductSpecification;
import com.woowa.woowakit.domain.product.domain.ProductStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<ProductSpecification> searchInStockProducts(final InStockProductSearchCondition condition) {
		return jpaQueryFactory
			.select(
				Projections.constructor(ProductSpecification.class,
					product,
					productSales.sale.value))
			.from(product)
			.leftJoin(productSales)
			.on(product.id.eq(productSales.productId).and(saleNow(condition.getSaleDate())))
			.fetchJoin()
			.where(
				containsName(condition.getProductKeyword()),
				sale(condition.getLastProductSale(), condition.getLastProductId()),
				product.status.eq(ProductStatus.IN_STOCK))
			.orderBy(productSales.sale.value.desc().nullsLast(), product.id.asc())
			.limit(condition.getPageSize())
			.fetch();
	}

	private BooleanExpression saleNow(final LocalDate localDate) {
		if (localDate == null) {
			return null;
		}
		return productSales.saleDate.eq(localDate);
	}

	private BooleanExpression sale(final Long sale, final Long productId) {
		if (productId == null) {
			return null;
		}
		if (sale == null) {
			return product.id.gt(productId);
		}

		return productSales.sale.value.lt(sale).or(productSales.sale.value.eq(sale).and(product.id.gt(productId)));
	}

	@Override
	public List<Product> searchAllProducts(final AllProductSearchCondition condition) {
		return jpaQueryFactory
			.selectFrom(product)
			.where(
				containsName(condition.getProductKeyword()),
				cursorId(condition.getLastProductId()))
			.orderBy(product.id.asc())
			.limit(condition.getPageSize())
			.fetch();
	}

	private BooleanExpression cursorId(final Long cursorId) {
		if (cursorId == null) {
			return null;
		}

		return order.id.gt(cursorId);
	}

	private BooleanExpression containsName(final String productKeyword) {
		if (StringUtils.hasText(productKeyword)) {
			return product.name.value.containsIgnoreCase(productKeyword);
		}

		return null;
	}
}
