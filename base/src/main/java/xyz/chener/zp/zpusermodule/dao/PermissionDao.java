package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.zpusermodule.entity.Permission;

/**
 * (Permission)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-11 15:22:34
 */

@Mapper
@CacheNamespace(implementation = xyz.chener.zp.common.config.cache.impl.DefaultRedissonMybatisCache.class)
public interface PermissionDao extends BaseMapper<Permission> {

}

