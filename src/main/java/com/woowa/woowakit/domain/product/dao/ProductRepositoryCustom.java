package com.woowa.woowakit.domain.product.dao;

import java.util.List;

import com.woowa.woowakit.domain.product.domain.AllProductSearchCondition;
import com.woowa.woowakit.domain.product.domain.InStockProductSearchCondition;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductSpecification;

public interface ProductRepositoryCustom {

	List<ProductSpecification> searchInStockProducts(final InStockProductSearchCondition condition);

	List<Product> searchAllProducts(final AllProductSearchCondition condition);
}
