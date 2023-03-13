package xyz.chener.zp.zpusermodule.config.oplog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OperateRecord;

/**
 * (OperateRecord)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-12 20:18:32
 */
public interface OperateRecordDao extends BaseMapper<OperateRecord> {

    int insertRecord(@Param("op") OperateRecord operateRecord);

}

