package xyz.chener.zp.datasharing.config.oplog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import xyz.chener.zp.datasharing.config.oplog.dao.OperateRecordDao;
import xyz.chener.zp.datasharing.config.oplog.entity.OperateRecord;
import xyz.chener.zp.datasharing.config.oplog.entity.OperateRecordDto;
import xyz.chener.zp.datasharing.config.oplog.service.OperateRecordService;

/**
 * (OperateRecord)表服务实现类
 *
 * @author chener
 * @since 2023-03-14 09:36:30
 */
@Service
public class OperateRecordServiceImpl extends ServiceImpl<OperateRecordDao, OperateRecord> implements OperateRecordService {

}

