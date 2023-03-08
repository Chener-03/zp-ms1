package xyz.chener.zp.zpusermodule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.chener.zp.zpusermodule.entity.Messages;
import xyz.chener.zp.zpusermodule.entity.dto.MessagesDto;

import java.util.List;

/**
 * (Messages)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-05 12:31:57
 */

@Mapper
public interface MessagesDao extends BaseMapper<Messages> {

    List<MessagesDto> getMessagesList(@Param("dto") MessagesDto messagesDto
            , @Param("username") String username,@Param("isReceive") Boolean isReceive);

}

