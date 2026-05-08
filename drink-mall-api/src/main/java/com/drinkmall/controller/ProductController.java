package com.drinkmall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.entity.Product;
import com.drinkmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public Result<Page<Product>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String zoneType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(productService.getProducts(categoryId, zoneType, keyword, minPrice, maxPrice, sortBy, page, size));
    }

    @GetMapping("/{productId}")
    public Result<Product> getProductDetail(@PathVariable Long productId) {
        return Result.success(productService.getProductDetail(productId));
    }
}
