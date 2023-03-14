package xyz.chener.zp.zpusermodule.config.oplog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OperateRecord;
import xyz.chener.zp.zpusermodule.entity.dto.OperateRecordDto;
import xyz.chener.zp.zpusermodule.entity.dto.UserLoginEventRecordDto;

/**
 * (OperateRecord)表服务接口
 *
 * @author chener
 * @since 2023-03-14 09:36:29
 */
public interface OperateRecordService extends IService<OperateRecord> {

    PageInfo<OperateRecordDto> getList(OperateRecordDto dto , Integer page , Integer size);


}

