package xyz.chener.zp.datasharing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.datasharing.entity.DsDatasource;
import xyz.chener.zp.datasharing.entity.dto.ConnectResult;
import xyz.chener.zp.datasharing.entity.dto.DsDatasourceDto;

/**
 * (DsDatasource)表服务接口
 *
 * @author makejava
 * @since 2023-03-31 22:51:40
 */
public interface DsDatasourceService extends IService<DsDatasource> {

    /**
     * 测试连接，成功返回NULL 失败返回错误信息
     * @param dsDatasource
     * @return
     */
    ConnectResult testConnection(DsDatasource dsDatasource);


    DsDatasource saveOrUpdateDataSource(DsDatasource dsDatasource);


    PageInfo<DsDatasourceDto> getList(DsDatasourceDto params, FieldQuery fieldQuery, Integer page, Integer size);


    Boolean flushAllDatasource();

    Boolean removeDataSource(Long id);

}

