package com.woowa.woowakit.domain.cart.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.cart.domain.CartItem;
import com.woowa.woowakit.domain.cart.domain.CartItemRepository;
import com.woowa.woowakit.domain.cart.domain.CartItemSpecification;
import com.woowa.woowakit.domain.cart.dto.CartItemAddRequest;
import com.woowa.woowakit.domain.cart.dto.CartItemUpdateQuantityRequest;
import com.woowa.woowakit.domain.cart.exception.CartItemNotExistException;
import com.woowa.woowakit.domain.cart.exception.NotMyCartItemException;
import com.woowa.woowakit.domain.cart.exception.ProductNotExistException;
import com.woowa.woowakit.domain.product.domain.product.Product;
import com.woowa.woowakit.domain.product.domain.product.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartItemService {

	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;

	@Transactional
	public CartItem addCartItem(final CartItemAddRequest request, final Long memberId) {
		final CartItem cartItem = getCartItemByMemberIdAndProductId(memberId, request.getProductId());

		cartItem.addQuantity(request.getQuantity());

		return cartItemRepository.save(cartItem);
	}

	public List<CartItemSpecification> readCartItem(final Long memberId) {
		return cartItemRepository.findCartItemByMemberId(memberId);
	}

	@Transactional
	public void deleteCartItems(final Long cartItemId, final Long memberId) {
		cartItemRepository.deleteCartItems(memberId, List.of(cartItemId));
	}

	@Transactional
	public void updateQuantity(
		final Long cartItemId,
		final CartItemUpdateQuantityRequest request,
		final Long memberId
	) {
		final CartItem cartItem = getCartItem(cartItemId);

		if (!cartItem.isMyCartItem(memberId)) {
			throw new NotMyCartItemException();
		}

		cartItem.updateQuantity(request.getQuantity());
	}

	private CartItem getCartItemByMemberIdAndProductId(final Long memberId, final Long productId) {
		return cartItemRepository.findCartItemByMemberIdAndProductId(memberId, productId)
			.orElse(CartItem.builder()
				.memberId(memberId)
				.product(getProduct(productId))
				.build());
	}

	private Product getProduct(final Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(ProductNotExistException::new);
	}

	private CartItem getCartItem(final Long cartItemId) {
		return cartItemRepository.findById(cartItemId)
			.orElseThrow(CartItemNotExistException::new);
	}
}
