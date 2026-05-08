package com.drinkmall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.entity.Banner;
import com.drinkmall.entity.Category;
import com.drinkmall.entity.Product;
import com.drinkmall.mapper.BannerMapper;
import com.drinkmall.mapper.CategoryMapper;
import com.drinkmall.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final BannerMapper bannerMapper;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        List<Banner> banners = bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getStatus, 1)
                        .orderByAsc(Banner::getSortOrder)
        );
        return Result.success(banners);
    }

    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSortOrder)
        );
        return Result.success(categories);
    }

    @GetMapping("/products")
    public Result<Page<Product>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String zoneType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (zoneType != null) {
            wrapper.eq(Product::getZoneType, zoneType);
        }

        wrapper.orderByDesc(Product::getSortOrder);

        Page<Product> result = productMapper.selectPage(pageParam, wrapper);
        return Result.success(result);
    }

    @GetMapping("/products/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() != 1) {
            return Result.success(null);
        }
        return Result.success(product);
    }
}
