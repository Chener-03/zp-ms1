package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.dao.MessagesDao;
import xyz.chener.zp.zpusermodule.entity.Messages;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.MessagesDto;
import xyz.chener.zp.zpusermodule.error.messages.OnlyGetSelfMessage;
import xyz.chener.zp.zpusermodule.error.messages.ThisMessageAlreadyDelete;
import xyz.chener.zp.zpusermodule.service.MessagesService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.service.UserBaseService;

import java.util.Date;

/**
 * (Messages)表服务实现类
 *
 * @author makejava
 * @since 2023-03-05 12:31:57
 */
@Service
public class MessagesServiceImpl extends ServiceImpl<MessagesDao, Messages> implements MessagesService {

    private final UserBaseService userBaseService;

    public MessagesServiceImpl(UserBaseService userBaseService) {
        this.userBaseService = userBaseService;
    }


    @Override
    public MessagesDto getUserMessageById(String username, Integer messageId) {
        UserBase user = userBaseService.lambdaQuery().select(UserBase::getId)
                .eq(UserBase::getUsername, username).one();
        Messages messages = this.lambdaQuery().eq(Messages::getId, messageId).eq(Messages::getUserId, user.getId()).one();
        AssertUrils.state(messages != null, OnlyGetSelfMessage.class);
        AssertUrils.state(!messages.getReceiveDelete() && messages.getIsdelete()==0, ThisMessageAlreadyDelete.class);
        MessagesDto messagesDto = new MessagesDto();
        ObjectUtils.copyFields(messages, messagesDto);
        if (!messages.getIsread()) {
            this.lambdaUpdate().set(Messages::getIsread, true)
                    .set(Messages::getReadTime, new Date())
                    .eq(Messages::getId, messageId).update();
        }
        userBaseService.lambdaQuery().select(UserBase::getUsername)
                .eq(UserBase::getId, messages.getSendUserId()).oneOpt().ifPresent(userBase -> {
            messagesDto.setSendUserName(userBase.getUsername());
        });
        if (messages.getRefMessageId() != null) {
            this.lambdaQuery().select(Messages::getTitle, Messages::getCreateTime)
                    .eq(Messages::getId, messages.getRefMessageId()).oneOpt().ifPresent(e->{
                        messagesDto.setRefMessageDate(e.getCreateTime());
                        messagesDto.setRefMessageTitle(e.getTitle());
                    });
        }
        return messagesDto;
    }
}

