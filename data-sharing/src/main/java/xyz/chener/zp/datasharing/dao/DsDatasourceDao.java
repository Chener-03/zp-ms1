package xyz.chener.zp.datasharing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.chener.zp.datasharing.entity.DsDatasource;
import xyz.chener.zp.datasharing.entity.dto.DsDatasourceDto;

import java.util.List;

/**
 * (DsDatasource)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-31 22:51:38
 */

@Mapper
public interface DsDatasourceDao extends BaseMapper<DsDatasource> {

    List<DsDatasourceDto> getList(@Param("params") DsDatasourceDto params,@Param("orgIds") List<String> orgIds);

}

