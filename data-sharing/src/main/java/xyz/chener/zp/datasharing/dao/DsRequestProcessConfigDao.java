package xyz.chener.zp.datasharing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.chener.zp.datasharing.entity.DsRequestProcessConfig;

/**
 * (DsRequestProcessConfig)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-02 10:24:08
 */
@Mapper
public interface DsRequestProcessConfigDao extends BaseMapper<DsRequestProcessConfig> {

    int saveOrUpdateByTypeAndConfigId(@Param("data")DsRequestProcessConfig data);

}

