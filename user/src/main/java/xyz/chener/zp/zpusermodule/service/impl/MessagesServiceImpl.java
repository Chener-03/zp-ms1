package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import java.util.List;

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
        UserBase user = userBaseService.lambdaQuery().select(UserBase::getId,UserBase::getUsername)
                .eq(UserBase::getUsername, username).one();
        Messages messages = this.lambdaQuery()
                .eq(Messages::getId, messageId)
                .and(e -> e.eq(Messages::getSendUserId, user.getId())
                        .or().eq(Messages::getUserId, user.getId())).one();
        AssertUrils.state(messages != null, OnlyGetSelfMessage.class);
        AssertUrils.state(!messages.getReceiveDelete() && messages.getIsdelete()==0, ThisMessageAlreadyDelete.class);
        MessagesDto messagesDto = new MessagesDto();
        ObjectUtils.copyFields(messages, messagesDto);
        if (!messages.getIsread() && user.getId().equals(messages.getUserId())) {
            this.lambdaUpdate().set(Messages::getIsread, true)
                    .set(Messages::getReadTime, new Date())
                    .eq(Messages::getId, messageId).update();
        }
        if (user.getId().equals(messages.getSendUserId())) {
            messagesDto.setSendUserName(user.getUsername());
            userBaseService.lambdaQuery().select(UserBase::getUsername)
                    .eq(UserBase::getId, messages.getUserId()).oneOpt().ifPresent(userBase -> {
                        messagesDto.setUsername(userBase.getUsername());
                    });
        }else {
            messagesDto.setUsername(user.getUsername());
            userBaseService.lambdaQuery().select(UserBase::getUsername)
                    .eq(UserBase::getId, messages.getSendUserId()).oneOpt().ifPresent(userBase -> {
                        messagesDto.setSendUserName(userBase.getUsername());
                    });
        }
        if (messages.getRefMessageId() != null) {
            this.lambdaQuery().select(Messages::getTitle, Messages::getCreateTime)
                    .eq(Messages::getId, messages.getRefMessageId()).oneOpt().ifPresent(e->{
                        messagesDto.setRefMessageDate(e.getCreateTime());
                        messagesDto.setRefMessageTitle(e.getTitle());
                    });
        }
        return messagesDto;
    }

    @Override
    public Boolean sendUsersMessage(MessagesDto messagesDto, List<Long> userIds,String sendUsername) {
        UserBase sendUser = userBaseService.lambdaQuery().select(UserBase::getId,UserBase::getUsername)
                .eq(UserBase::getUsername, sendUsername).one();
        userIds.forEach(e->{
            Messages message = new Messages();
            ObjectUtils.copyFields(messagesDto, message);
            message.setSendUserId(sendUser.getId());
            message.setType(Messages.Type.OTHER);
            message.setIsread(false);
            message.setCreateTime(new Date());
            message.setSenderDelete(false);
            message.setReceiveDelete(false);
            message.setUserId(e);
            save(message);
        });
        return true;
    }

    @Override
    public PageInfo<MessagesDto> getMessagesList(MessagesDto messagesDto, String username
            , Boolean isReceive,Integer page,Integer size) {
        PageHelper.startPage(page,size);
        return new PageInfo<>(getBaseMapper().getMessagesList(messagesDto,username,isReceive));
    }
}

