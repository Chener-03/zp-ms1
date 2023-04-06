package xyz.chener.zp.datasharing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import xyz.chener.zp.datasharing.entity.DsRequestConfig;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigDto;

/**
 * (DsRequestConfig)表服务接口
 *
 * @author makejava
 * @since 2023-04-02 10:23:55
 */
public interface DsRequestConfigService extends IService<DsRequestConfig> {

    PageInfo<DsRequestConfigDto> getDsRequestConfigList(DsRequestConfigDto dsRequestConfigDto,int page,int size);

}

