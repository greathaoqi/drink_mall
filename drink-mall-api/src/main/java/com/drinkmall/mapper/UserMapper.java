package com.drinkmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drinkmall.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
