package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.chener.zp.zpusermodule.entity.Notices;
import xyz.chener.zp.zpusermodule.entity.dto.NoticesDto;

import java.util.List;

/**
 * (Notices)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-04 18:21:14
 */

@Mapper
@CacheNamespace(implementation = xyz.chener.zp.common.config.cache.impl.DefaultRedissonMybatisCache.class)
public interface NoticesDao extends BaseMapper<Notices> {

    List<NoticesDto> getList(@Param("pm") NoticesDto noticesDto);

}

