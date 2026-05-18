package com.drinkmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drinkmall.entity.ContentLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for content_likes table.
 */
@Mapper
public interface ContentLikeMapper extends BaseMapper<ContentLike> {
}