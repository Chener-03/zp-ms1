package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.zpusermodule.entity.UserLoginEventRecord;

/**
 * (UserLoginEventRecord)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-16 15:59:34
 */
@Mapper
public interface UserLoginEventRecordDao extends BaseMapper<UserLoginEventRecord> {

}

