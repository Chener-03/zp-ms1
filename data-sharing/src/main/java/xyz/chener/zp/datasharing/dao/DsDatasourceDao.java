package xyz.chener.zp.datasharing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.datasharing.entity.DsDatasource;

/**
 * (DsDatasource)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-31 22:51:38
 */

@Mapper
public interface DsDatasourceDao extends BaseMapper<DsDatasource> {

}

