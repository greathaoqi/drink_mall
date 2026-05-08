package com.drinkmall.service;

import com.drinkmall.dto.*;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddresses(Long userId);
    AddressResponse getAddress(Long userId, Long addressId);
    AddressResponse getDefaultAddress(Long userId);
    AddressResponse addAddress(Long userId, AddressRequest request);
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    void setDefault(Long userId, Long addressId);
}
