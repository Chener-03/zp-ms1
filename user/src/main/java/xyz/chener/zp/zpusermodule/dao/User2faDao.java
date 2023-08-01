package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.zpusermodule.entity.User2fa;

/**
 * (User2fa)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-25 20:21:47
 */

@Mapper
public interface User2faDao extends BaseMapper<User2fa> {

}

