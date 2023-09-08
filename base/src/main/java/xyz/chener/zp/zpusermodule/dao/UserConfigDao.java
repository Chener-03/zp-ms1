package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.zpusermodule.entity.UserConfig;

/**
 * (UserConfig)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-11 19:12:35
 */
@Mapper
@CacheNamespace(implementation = xyz.chener.zp.common.config.cache.impl.DefaultRedissonMybatisCache.class)
public interface UserConfigDao extends BaseMapper<UserConfig> {

}

