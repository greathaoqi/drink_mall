package com.drinkmall.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.entity.Category;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.StockLog;
import com.drinkmall.mapper.CategoryMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.mapper.StockLogMapper;
import com.drinkmall.service.admin.AdminProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final StockLogMapper stockLogMapper;

    @Override
    public Page<Product> getProducts(Long categoryId, String zoneType, Integer status, String keyword, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) wrapper.eq(Product::getCategoryId, categoryId);
        if (zoneType != null) wrapper.eq(Product::getZoneType, zoneType);
        if (status != null) wrapper.eq(Product::getStatus, status);
        if (keyword != null) wrapper.like(Product::getName, keyword);
        wrapper.orderByDesc(Product::getCreatedAt);
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Product getProductDetail(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setSales(0);
        productMapper.insert(product);
        logStockChange(product.getId(), product.getStock(), "创建商品初始库存");
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Product product) {
        Product existing = productMapper.selectById(product.getId());
        if (existing == null) throw new BusinessException(404, "商品不存在");
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        if (product.getStock() != null && !product.getStock().equals(existing.getStock())) {
            int diff = product.getStock() - existing.getStock();
            logStockChange(product.getId(), diff, "管理员调整库存");
        }
        return product;
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        productMapper.deleteById(productId);
    }

    @Override
    public void updateStatus(Long productId, Integer status) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException(404, "商品不存在");
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public void adjustStock(Long productId, Integer quantity, String reason) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException(404, "商品不存在");
        product.setStock(product.getStock() + quantity);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        logStockChange(productId, quantity, reason);
    }

    @Override
    public Page<StockLog> getStockLogs(Long productId, Integer page, Integer size) {
        Page<StockLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<StockLog> wrapper = new LambdaQueryWrapper<StockLog>()
                .eq(StockLog::getProductId, productId)
                .orderByDesc(StockLog::getCreatedAt);
        return stockLogMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<Category> getCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
    }

    @Override
    public Category createCategory(Category category) {
        category.setCreatedAt(LocalDateTime.now());
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category updateCategory(Category category) {
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
        return category;
    }

    @Override
    public void deleteCategory(Long categoryId) {
        int count = productMapper.selectCount(new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, categoryId));
        if (count > 0) throw new BusinessException(400, "该分类下有商品，无法删除");
        categoryMapper.deleteById(categoryId);
    }

    private void logStockChange(Long productId, Integer quantity, String reason) {
        StockLog log = new StockLog();
        log.setProductId(productId);
        log.setQuantity(quantity);
        log.setReason(reason);
        log.setCreatedAt(LocalDateTime.now());
        stockLogMapper.insert(log);
    }
}