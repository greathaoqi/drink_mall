package com.drinkmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.*;
import com.drinkmall.entity.Product;
import java.math.BigDecimal;

public interface ProductService {
    Page<Product> getProducts(Long categoryId, String zoneType, String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortBy, Integer page, Integer size);
    Product getProductDetail(Long productId);
    void updateStock(Long productId, Integer quantity, String changeType, Long orderId, String operator, String remark);
}
