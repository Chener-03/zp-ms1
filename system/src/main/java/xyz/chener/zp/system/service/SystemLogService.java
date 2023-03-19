package xyz.chener.zp.system.service;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.common.entity.vo.PageInfo;
import xyz.chener.zp.system.entity.dto.LogEntityDto;

public interface SystemLogService {

    PageInfo<LogEntityDto> getAppLogs( LogEntityDto dto , Integer page ,  Integer size);

}
