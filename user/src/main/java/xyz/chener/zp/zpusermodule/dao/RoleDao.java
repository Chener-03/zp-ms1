package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.chener.zp.zpusermodule.entity.Role;

/**
 * (Role)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-11 15:22:52
 */
@Mapper
public interface RoleDao extends BaseMapper<Role> {

}

