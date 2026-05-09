package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.Category;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.StockLog;
import java.util.List;

public interface AdminProductService {
    Page<Product> getProducts(Long categoryId, String zoneType, Integer status, String keyword, Integer page, Integer size);
    Product getProductDetail(Long productId);
    Product createProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Long productId);
    void updateStatus(Long productId, Integer status);
    void adjustStock(Long productId, Integer quantity, String reason);
    Page<StockLog> getStockLogs(Long productId, Integer page, Integer size);
    List<Category> getCategories();
    Category createCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(Long categoryId);
}
