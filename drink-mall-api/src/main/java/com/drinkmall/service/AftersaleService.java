package com.drinkmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.Aftersale;

public interface AftersaleService {
    Aftersale applyAftersale(Long userId, Long orderId, String type, String reason, String description);
    Page<Aftersale> getUserAftersales(Long userId, Integer page, Integer size);
    Aftersale getAftersaleDetail(Long userId, Long aftersaleId);
}
