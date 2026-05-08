package com.drinkmall.dto;

import com.drinkmall.entity.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Long id;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Boolean isDefault;
    private String fullAddress;

    public static AddressResponse fromEntity(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .name(address.getName())
                .phone(address.getPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detail(address.getDetail())
                .isDefault(address.getIsDefault())
                .fullAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail())
                .build();
    }
}
