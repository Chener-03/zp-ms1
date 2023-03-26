package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.zpusermodule.entity.Dictionaries;

/**
 * (Dictionaries)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-28 11:52:12
 */
@Mapper
@CacheNamespace(implementation = xyz.chener.zp.common.config.cache.impl.DefaultRedissonMybatisCache.class)
public interface DictionariesDao extends BaseMapper<Dictionaries> {

}

