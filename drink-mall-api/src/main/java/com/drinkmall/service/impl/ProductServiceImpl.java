package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.*;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.StockLog;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.mapper.StockLogMapper;
import com.drinkmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final StockLogMapper stockLogMapper;

    @Override
    public Page<Product> getProducts(Long categoryId, String zoneType, String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortBy, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (zoneType != null) {
            wrapper.eq(Product::getZoneType, zoneType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName, keyword);
        }
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }

        if ("sales".equals(sortBy)) {
            wrapper.orderByDesc(Product::getSales);
        } else if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            wrapper.orderByDesc(Product::getSortOrder);
        }

        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Product getProductDetail(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException(404, "商品不存在");
        }
        return product;
    }

    @Override
    @Transactional
    public void updateStock(Long productId, Integer quantity, String changeType, Long orderId, String operator, String remark) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        int beforeStock = product.getStock();
        int afterStock = beforeStock + quantity;

        if (afterStock < 0) {
            throw new BusinessException(400, "库存不足");
        }

        product.setStock(afterStock);
        productMapper.updateById(product);

        StockLog log = new StockLog();
        log.setProductId(productId);
        log.setChangeType(changeType);
        log.setChangeQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOrderId(orderId);
        log.setOperator(operator);
        log.setRemark(remark);
        stockLogMapper.insert(log);
    }
}
