package xyz.chener.zp.zpstoragecalculation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.zpstoragecalculation.entity.FileSystemMap;

/**
 * (FileSystemMap)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-22 22:26:33
 */

@Mapper
@CacheNamespace(implementation = xyz.chener.zp.common.config.cache.impl.DefaultRedissonMybatisCache.class)
public interface FileSystemMapDao extends BaseMapper<FileSystemMap> {

}

