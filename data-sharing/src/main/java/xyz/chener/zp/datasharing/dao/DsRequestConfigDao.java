package xyz.chener.zp.datasharing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.chener.zp.datasharing.entity.DsRequestConfig;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigDto;

import java.util.List;

/**
 * (DsRequestConfig)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-02 10:23:54
 */
@Mapper
public interface DsRequestConfigDao extends BaseMapper<DsRequestConfig> {

    List<DsRequestConfigDto> getRequestConfigList(@Param("pm") DsRequestConfigDto requestConfigDto,@Param("orgIds") List<String> orgIds);


}

