package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.zpusermodule.entity.UserExtend;

/**
 * (UserExtend)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-19 15:19:57
 */
@Mapper
public interface UserExtendDao extends BaseMapper<UserExtend> {

}

