package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.chener.zp.zpusermodule.entity.Messages;
import xyz.chener.zp.zpusermodule.entity.dto.MessagesDto;

/**
 * (Messages)表服务接口
 *
 * @author makejava
 * @since 2023-03-05 12:31:57
 */
public interface MessagesService extends IService<Messages> {

    MessagesDto getUserMessageById(String  username,Integer messageId);

}

