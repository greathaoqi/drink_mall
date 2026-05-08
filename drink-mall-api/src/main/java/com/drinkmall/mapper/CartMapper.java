package com.drinkmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drinkmall.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
