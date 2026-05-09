package com.drinkmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drinkmall.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {}