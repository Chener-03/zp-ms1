package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.UserAllInfoDto;

import java.util.List;

/**
 * (UserBase)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-11 15:23:15
 */
@Mapper
public interface UserBaseDao extends BaseMapper<UserBase> {

    List<UserAllInfoDto> getAllUserInfo(@Param("userAllInfo") UserAllInfoDto userAllInfo
            ,@Param("roleNotNull") Boolean roleNotNull);

}

