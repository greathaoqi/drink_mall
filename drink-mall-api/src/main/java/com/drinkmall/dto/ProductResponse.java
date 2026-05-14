package com.drinkmall.dto;

import com.drinkmall.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String mainImage;
    private List<String> images;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String levelDiscountText;
    private Integer giftPoints;
    private Integer stock;
    private Integer sales;
    private String zoneType;
    private String payMethodText;
    private String upgradeText;
    private Integer pointsPrice;
    private List<PaymentMethodResponse> payMethods;

    public static ProductResponse fromProduct(Product product, List<PaymentMethodResponse> payMethods) {
        if (product == null) {
            return null;
        }
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .mainImage(product.getMainImage())
                .images(splitImages(product.getImages()))
                .price(product.getPrice())
                .discountPrice(product.getPrice())
                .levelDiscountText("")
                .giftPoints(product.getGiftPoints())
                .stock(product.getStock())
                .sales(product.getSales())
                .zoneType(product.getZoneType())
                .payMethodText(payMethods.stream().map(PaymentMethodResponse::getText).collect(Collectors.joining("、")))
                .upgradeText("")
                .pointsPrice(product.getGiftPointsPrice())
                .payMethods(payMethods)
                .build();
    }

    private static List<String> splitImages(String images) {
        if (images == null || images.isBlank()) {
            return List.of();
        }
        return Arrays.stream(images.split(","))
                .map(String::trim)
                .filter(image -> !image.isBlank())
                .toList();
    }
}
