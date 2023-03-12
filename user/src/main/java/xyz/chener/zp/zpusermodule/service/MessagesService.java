package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.zpusermodule.entity.Messages;
import xyz.chener.zp.zpusermodule.entity.dto.MessagesDto;

import java.util.List;

/**
 * (Messages)表服务接口
 *
 * @author makejava
 * @since 2023-03-05 12:31:57
 */
public interface MessagesService extends IService<Messages> {

    MessagesDto getUserMessageById(String  username, Integer messageId, Boolean isReceive, FieldQuery fieldQuery);

    Boolean sendUsersMessage( MessagesDto messagesDto,List<Long> userIds,String sendUsername);

    PageInfo<MessagesDto> getMessagesList(MessagesDto messagesDto, String username
            , Boolean isReceive, Integer page, Integer size);

    Boolean removeMessage(Integer messageId, String username,Boolean isReceive);
}

