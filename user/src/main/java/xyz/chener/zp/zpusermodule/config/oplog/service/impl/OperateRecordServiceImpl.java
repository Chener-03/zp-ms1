package xyz.chener.zp.zpusermodule.config.oplog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import xyz.chener.zp.zpusermodule.config.oplog.dao.OperateRecordDao;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OperateRecord;
import xyz.chener.zp.zpusermodule.config.oplog.service.OperateRecordService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.entity.dto.OperateRecordDto;

/**
 * (OperateRecord)表服务实现类
 *
 * @author chener
 * @since 2023-03-14 09:36:30
 */
@Service
public class OperateRecordServiceImpl extends ServiceImpl<OperateRecordDao, OperateRecord> implements OperateRecordService {

    @Override
    public PageInfo<OperateRecordDto> getList(OperateRecordDto dto, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        return new PageInfo<>(this.baseMapper.getList(dto));
    }
}

