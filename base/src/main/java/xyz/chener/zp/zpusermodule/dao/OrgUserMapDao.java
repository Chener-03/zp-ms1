package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.chener.zp.zpusermodule.entity.OrgBase;
import xyz.chener.zp.zpusermodule.entity.OrgUserMap;
import xyz.chener.zp.zpusermodule.entity.dto.OrgUserDto;

import java.util.List;

/**
 * (OrgUserMap)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-21 11:55:56
 */
@Mapper
@CacheNamespace(implementation = xyz.chener.zp.common.config.cache.impl.DefaultRedissonMybatisCache.class)
public interface OrgUserMapDao extends BaseMapper<OrgUserMap> {

    List<OrgUserDto> getOrgUsers(@Param("id") Long id);

    OrgBase getOrgBaseByUserId(@Param("userId") Long userId);

}

