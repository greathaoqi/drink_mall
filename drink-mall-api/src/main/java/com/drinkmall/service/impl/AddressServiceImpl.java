package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.*;
import com.drinkmall.entity.Address;
import com.drinkmall.mapper.AddressMapper;
import com.drinkmall.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<AddressResponse> getAddresses(Long userId) {
        List<Address> addresses = addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreatedAt)
        );

        long count = addresses.size();
        if (count > 10) {
            throw new BusinessException(400, "收货地址最多保存10个");
        }

        return addresses.stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse getAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectOne(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getId, addressId)
                        .eq(Address::getUserId, userId)
        );

        if (address == null) {
            throw new BusinessException(404, "地址不存在");
        }

        return AddressResponse.fromEntity(address);
    }

    @Override
    public AddressResponse getDefaultAddress(Long userId) {
        Address address = addressMapper.selectOne(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getIsDefault, true)
        );

        return address != null ? AddressResponse.fromEntity(address) : null;
    }

    @Override
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        long count = addressMapper.selectCount(
                new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId)
        );

        if (count >= 10) {
            throw new BusinessException(400, "收货地址最多保存10个");
        }

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressMapper.update(null,
                    new LambdaUpdateWrapper<Address>()
                            .eq(Address::getUserId, userId)
                            .set(Address::getIsDefault, false)
            );
        }

        Address address = new Address();
        address.setUserId(userId);
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        addressMapper.insert(address);

        return AddressResponse.fromEntity(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressMapper.selectOne(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getId, addressId)
                        .eq(Address::getUserId, userId)
        );

        if (address == null) {
            throw new BusinessException(404, "地址不存在");
        }

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressMapper.update(null,
                    new LambdaUpdateWrapper<Address>()
                            .eq(Address::getUserId, userId)
                            .set(Address::getIsDefault, false)
            );
        }

        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : address.getIsDefault());
        address.setUpdatedAt(LocalDateTime.now());
        addressMapper.updateById(address);

        return AddressResponse.fromEntity(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        addressMapper.delete(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getId, addressId)
                        .eq(Address::getUserId, userId)
        );
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long addressId) {
        Address address = addressMapper.selectOne(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getId, addressId)
                        .eq(Address::getUserId, userId)
        );

        if (address == null) {
            throw new BusinessException(404, "地址不存在");
        }

        addressMapper.update(null,
                new LambdaUpdateWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .set(Address::getIsDefault, false)
        );

        address.setIsDefault(true);
        addressMapper.updateById(address);
    }
}
