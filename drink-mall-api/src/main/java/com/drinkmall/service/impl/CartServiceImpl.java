package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.*;
import com.drinkmall.entity.Cart;
import com.drinkmall.entity.Product;
import com.drinkmall.mapper.CartMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    public CartResponse getCart(Long userId) {
        List<Cart> carts = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId)
        );

        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal selectedPrice = BigDecimal.ZERO;
        int totalCount = 0;

        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product != null && product.getStatus() == 1) {
                CartItemResponse item = CartItemResponse.builder()
                        .cartId(cart.getId())
                        .productId(product.getId())
                        .productName(product.getName())
                        .productImage(product.getMainImage())
                        .price(product.getPrice())
                        .quantity(cart.getQuantity())
                        .stock(product.getStock())
                        .selected(cart.getSelected())
                        .build();
                items.add(item);

                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                totalPrice = totalPrice.add(itemTotal);
                totalCount += cart.getQuantity();

                if (Boolean.TRUE.equals(cart.getSelected())) {
                    selectedPrice = selectedPrice.add(itemTotal);
                }
            }
        }

        return CartResponse.builder()
                .items(items)
                .totalCount(totalCount)
                .totalPrice(totalPrice)
                .selectedPrice(selectedPrice)
                .build();
    }

    @Override
    @Transactional
    public void addToCart(Long userId, AddToCartRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException(404, "商品不存在");
        }

        if (request.getQuantity() > product.getStock()) {
            throw new BusinessException(400, "库存不足");
        }

        Cart cart = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .eq(Cart::getProductId, request.getProductId())
        );

        if (cart != null) {
            int newQuantity = cart.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStock()) {
                throw new BusinessException(400, "库存不足");
            }
            cart.setQuantity(newQuantity);
            cartMapper.updateById(cart);
        } else {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(request.getProductId());
            cart.setQuantity(request.getQuantity());
            cart.setSelected(true);
            cartMapper.insert(cart);
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long userId, Long cartId, Integer quantity) {
        Cart cart = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getId, cartId)
                        .eq(Cart::getUserId, userId)
        );

        if (cart == null) {
            throw new BusinessException(404, "购物车商品不存在");
        }

        Product product = productMapper.selectById(cart.getProductId());
        if (quantity > product.getStock()) {
            throw new BusinessException(400, "库存不足");
        }

        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long cartId) {
        cartMapper.delete(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getId, cartId)
                        .eq(Cart::getUserId, userId)
        );
    }

    @Override
    @Transactional
    public void updateSelected(Long userId, Long cartId, Boolean selected) {
        cartMapper.update(null,
                new LambdaUpdateWrapper<Cart>()
                        .eq(Cart::getId, cartId)
                        .eq(Cart::getUserId, userId)
                        .set(Cart::getSelected, selected)
        );
    }

    @Override
    @Transactional
    public void selectAll(Long userId, Boolean selected) {
        cartMapper.update(null,
                new LambdaUpdateWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .set(Cart::getSelected, selected)
        );
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartMapper.delete(
                new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId)
        );
    }
}
