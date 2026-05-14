package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.entity.Category;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.StockLog;
import com.drinkmall.service.admin.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/product")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping("/list")
    public Result<Page<Product>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String zoneType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminProductService.getProducts(categoryId, zoneType, status, keyword, page, size));
    }

    @GetMapping("/{productId}")
    public Result<Product> getProductDetail(@PathVariable Long productId) {
        return Result.success(adminProductService.getProductDetail(productId));
    }

    @PostMapping
    public Result<Product> createProduct(@RequestBody Product product) {
        return Result.success(adminProductService.createProduct(product));
    }

    @PutMapping("/{productId}")
    public Result<Product> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        product.setId(productId);
        return Result.success(adminProductService.updateProduct(product));
    }

    @DeleteMapping("/{productId}")
    public Result<Void> deleteProduct(@PathVariable Long productId) {
        adminProductService.deleteProduct(productId);
        return Result.success(null);
    }

    @PutMapping("/{productId}/status")
    public Result<Void> updateStatus(@PathVariable Long productId, @RequestParam Integer status) {
        adminProductService.updateStatus(productId, status);
        return Result.success(null);
    }

    @PutMapping("/{productId}/zone")
    public Result<Void> updateZone(
            @PathVariable Long productId,
            @RequestParam String zoneType,
            @RequestParam(required = false) String investmentLevelCode,
            @RequestParam(required = false) Integer giftPointsPrice) {
        adminProductService.updateZone(productId, zoneType, investmentLevelCode, giftPointsPrice);
        return Result.success(null);
    }

    @PutMapping("/{productId}/payment-methods")
    public Result<Void> updatePaymentMethods(
            @PathVariable Long productId,
            @RequestParam String allowedPaymentMethods,
            @RequestParam(required = false) Boolean wineBeanPayable) {
        adminProductService.updatePaymentMethods(productId, allowedPaymentMethods, wineBeanPayable);
        return Result.success(null);
    }

    @PutMapping("/{productId}/stock")
    public Result<Void> adjustStock(@PathVariable Long productId, @RequestParam Integer quantity, @RequestParam String reason) {
        adminProductService.adjustStock(productId, quantity, reason);
        return Result.success(null);
    }

    @GetMapping("/{productId}/stock-logs")
    public Result<Page<StockLog>> getStockLogs(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminProductService.getStockLogs(productId, page, size));
    }

    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        return Result.success(adminProductService.getCategories());
    }

    @PostMapping("/categories")
    public Result<Category> createCategory(@RequestBody Category category) {
        return Result.success(adminProductService.createCategory(category));
    }

    @PutMapping("/categories/{categoryId}")
    public Result<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        category.setId(categoryId);
        return Result.success(adminProductService.updateCategory(category));
    }

    @DeleteMapping("/categories/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        adminProductService.deleteCategory(categoryId);
        return Result.success(null);
    }
}
