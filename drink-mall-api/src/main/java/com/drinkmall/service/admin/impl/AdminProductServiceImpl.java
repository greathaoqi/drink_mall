package com.drinkmall.service.admin.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.entity.Category;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.StockLog;
import com.drinkmall.mapper.CategoryMapper;
import com.drinkmall.mapper.OperationLogMapper;
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
    private final OperationLogMapper operationLogMapper;

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
        logStockChange(product.getId(), product.getStock(), "create", "创建商品初始库存");
        logOperation("create", product.getId(), "创建商品");
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
            logStockChange(product.getId(), diff, "adjust", "管理员调整库存");
        }
        logOperation("update", product.getId(), "编辑商品");
        return product;
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        productMapper.deleteById(productId);
        logOperation("delete", productId, "删除商品");
    }

    @Override
    @Transactional
    public void updateStatus(Long productId, Integer status, String reason) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException(404, "商品不存在");
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        logOperation("status", productId, "status=" + status + ", reason=" + reason);
    }

    @Override
    @Transactional
    public void updateZone(Long productId, String zoneType, String investmentLevelCode, Integer giftPointsPrice) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException(404, "商品不存在");
        product.setZoneType(zoneType);
        product.setInvestmentLevelCode(investmentLevelCode);
        product.setGiftPointsPrice(giftPointsPrice);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        logOperation("zone", productId, "zoneType=" + zoneType + ", investmentLevelCode=" + investmentLevelCode + ", giftPointsPrice=" + giftPointsPrice);
    }

    @Override
    @Transactional
    public void updatePaymentMethods(Long productId, String allowedPaymentMethods, Boolean wineBeanPayable, String reason) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException(404, "商品不存在");
        product.setAllowedPaymentMethods(allowedPaymentMethods);
        product.setWineBeanPayable(Boolean.TRUE.equals(wineBeanPayable));
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        logOperation("payment_methods", productId, "allowedPaymentMethods=" + allowedPaymentMethods + ", wineBeanPayable=" + wineBeanPayable + ", reason=" + reason);
    }

    @Override
    @Transactional
    public void adjustStock(Long productId, Integer quantity, String reason) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException(404, "商品不存在");
        product.setStock(product.getStock() + quantity);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        logStockChange(productId, quantity, "adjust", reason);
        logOperation("stock", productId, reason);
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
    @Transactional
    public Category createCategory(Category category) {
        category.setCreatedAt(LocalDateTime.now());
        categoryMapper.insert(category);
        logOperation("category_create", category.getId(), "创建分类");
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
        logOperation("category_update", category.getId(), "编辑分类");
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        long count = productMapper.selectCount(new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, categoryId));
        if (count > 0) throw new BusinessException(400, "该分类下有商品，无法删除");
        categoryMapper.deleteById(categoryId);
        logOperation("category_delete", categoryId, "删除分类");
    }

    private void logStockChange(Long productId, Integer quantity, String changeType, String reason) {
        Product product = productMapper.selectById(productId);
        int afterStock = product == null || product.getStock() == null ? 0 : product.getStock();
        int beforeStock = afterStock - (quantity == null ? 0 : quantity);
        StockLog log = new StockLog();
        log.setProductId(productId);
        log.setChangeType(changeType);
        log.setChangeQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOperator(String.valueOf(currentAdminId()));
        log.setRemark(reason);
        log.setCreatedAt(LocalDateTime.now());
        stockLogMapper.insert(log);
    }

    private void logOperation(String action, Long targetId, String detail) {
        OperationLog log = new OperationLog();
        log.setAdminUserId(currentAdminId());
        log.setModule("product");
        log.setAction(action);
        log.setTargetId(String.valueOf(targetId));
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    private Long currentAdminId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception ignored) {
            return null;
        }
    }
}
