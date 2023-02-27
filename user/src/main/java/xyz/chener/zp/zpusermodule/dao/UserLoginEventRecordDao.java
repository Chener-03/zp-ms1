package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.chener.zp.zpusermodule.entity.UserLoginEventRecord;
import xyz.chener.zp.zpusermodule.entity.dto.UserLoginEventRecordDto;

/**
 * (UserLoginEventRecord)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-16 15:59:34
 */
@Mapper
public interface UserLoginEventRecordDao extends BaseMapper<UserLoginEventRecord> {

    UserLoginEventRecordDto getList(@Param("dto") UserLoginEventRecordDto dto);

}

