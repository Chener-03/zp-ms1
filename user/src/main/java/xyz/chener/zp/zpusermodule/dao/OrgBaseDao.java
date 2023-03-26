package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.zpusermodule.entity.OrgBase;

/**
 * (OrgBase)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-16 09:43:14
 */

@Mapper
@CacheNamespace(implementation = xyz.chener.zp.common.config.cache.impl.DefaultRedissonMybatisCache.class)
public interface OrgBaseDao extends BaseMapper<OrgBase> {

}

