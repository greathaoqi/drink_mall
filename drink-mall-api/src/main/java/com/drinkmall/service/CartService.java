package com.drinkmall.service;

import com.drinkmall.dto.*;

public interface CartService {
    CartResponse getCart(Long userId);
    void addToCart(Long userId, AddToCartRequest request);
    void updateQuantity(Long userId, Long cartId, Integer quantity);
    void removeFromCart(Long userId, Long cartId);
    void updateSelected(Long userId, Long cartId, Boolean selected);
    void selectAll(Long userId, Boolean selected);
    void clearCart(Long userId);
}
