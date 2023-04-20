package xyz.chener.zp.datasharing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.datasharing.entity.DsRequestConfig;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigAllDto;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigDto;

import java.util.List;

/**
 * (DsRequestConfig)表服务接口
 *
 * @author makejava
 * @since 2023-04-02 10:23:55
 */
public interface DsRequestConfigService extends IService<DsRequestConfig> {

    PageInfo<DsRequestConfigDto> getDsRequestConfigList(DsRequestConfigDto dsRequestConfigDto,int page,int size);

    List<String>[] getSqlResultParam(String sql,Integer datasourceId);

    Boolean save(DsRequestConfigAllDto dto);

    DsRequestConfigAllDto getDetail(Integer id);

    Boolean delete(Integer id);


    String getDocumentMD(Integer id);

}

