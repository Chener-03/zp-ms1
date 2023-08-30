package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.zpusermodule.entity.UserLoginEventRecord;
import xyz.chener.zp.zpusermodule.entity.dto.UserLoginEventRecordDto;

/**
 * (UserLoginEventRecord)表服务接口
 *
 * @author makejava
 * @since 2023-01-16 15:59:34
 */
public interface UserLoginEventRecordService extends IService<UserLoginEventRecord> {

    PageInfo<UserLoginEventRecordDto> getList( UserLoginEventRecordDto dto , Integer page ,  Integer size);

}

