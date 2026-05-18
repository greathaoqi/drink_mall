package com.drinkmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drinkmall.entity.ContentCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for content_categories table.
 */
@Mapper
public interface ContentCategoryMapper extends BaseMapper<ContentCategory> {
}