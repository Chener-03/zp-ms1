package xyz.chener.zp.zpusermodule.config.oplog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xyz.chener.zp.zpusermodule.config.oplog.dao.OperateRecordDao;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OperateRecord;
import xyz.chener.zp.zpusermodule.config.oplog.service.OperateRecordService;
import org.springframework.stereotype.Service;

/**
 * (OperateRecord)表服务实现类
 *
 * @author makejava
 * @since 2023-03-12 20:18:39
 */
@Service("operateRecordService")
public class OperateRecordServiceImpl extends ServiceImpl<OperateRecordDao, OperateRecord> implements OperateRecordService {

}

