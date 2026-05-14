package com.drinkmall.dto;

import com.drinkmall.entity.Product;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.enums.ProductZoneType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MiniProgramContractTest {

    @Test
    void loginRequestBuildsRegisterSourceFromSceneWhenSourceMissing() {
        LoginRequest request = new LoginRequest();
        request.setScene("share_product");
        request.setSourceProductId(100L);

        assertThat(request.resolveRegisterSource()).isEqualTo("share_product:100");
    }

    @Test
    void productResponseExposesConfiguredPayMethodTextAndPointsPrice() {
        Product product = new Product();
        product.setId(8L);
        product.setName("gift");
        product.setZoneType(ProductZoneType.GIFT.getCode());
        product.setPrice(new BigDecimal("99.00"));
        product.setAllowedPaymentMethods("points");
        product.setGiftPointsPrice(199);

        ProductResponse response = ProductResponse.fromProduct(product, List.of(PaymentMethodResponse.points()));

        assertThat(response.getZoneType()).isEqualTo("gift");
        assertThat(response.getPayMethodText()).isEqualTo("积分兑换");
        assertThat(response.getPointsPrice()).isEqualTo(199);
    }

    @Test
    void systemConfigResponseConvertsOptionVisibility() {
        SysConfig config = new SysConfig();
        config.setConfigKey("asset.option.visible_in_mini");
        config.setConfigValue("true");

        SystemConfigResponse response = SystemConfigResponse.fromConfigs(List.of(config));

        assertThat(response.getShowOption()).isTrue();
        assertThat(response.getConfigs()).containsEntry("asset.option.visible_in_mini", "true");
    }
}
