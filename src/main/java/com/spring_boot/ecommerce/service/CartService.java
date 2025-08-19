package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    List<CartDTO> getAllCarts();

    CartDTO addProdcutToCart(Long productId, Integer quantity);

    CartDTO getCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}
