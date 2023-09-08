package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.common.config.query.QueryHelper;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.common.utils.TransactionUtils;
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

    private DataSourceTransactionManager transactionManager;

    @Autowired
    public void setTransactionManager(DataSourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public MessagesDto getUserMessageById(String username, Integer messageId, Boolean isReceive,FieldQuery fieldQuery) {
        UserBase user = userBaseService.lambdaQuery().select(UserBase::getId,UserBase::getUsername)
                .eq(UserBase::getUsername, username).one();
        Messages messages = null;
        QueryHelper.StartQuery(fieldQuery, Messages.class);
        if (isReceive){
            messages = this.lambdaQuery().eq(Messages::getId, messageId)
                    .eq(Messages::getUserId, user.getId())
                    .one();
        }else {
            messages = this.lambdaQuery().eq(Messages::getId, messageId)
                    .eq(Messages::getSendUserId, user.getId())
                    .one();
        }

        AssertUrils.state(messages != null, OnlyGetSelfMessage.class);
        if (isReceive) {
            AssertUrils.state(messages.getIsdelete() == 0 && !messages.getReceiveDelete(), ThisMessageAlreadyDelete.class);
        }else {
            AssertUrils.state(messages.getIsdelete() == 0 && !messages.getSenderDelete(), ThisMessageAlreadyDelete.class);
        }
        MessagesDto messagesDto = new MessagesDto();
        ObjectUtils.copyFields(messages, messagesDto);
        if (!messages.getIsread() && user.getId().equals(messages.getUserId())) {
            this.lambdaUpdate().set(Messages::getIsread, true)
                    .set(Messages::getReadTime, new Date())
                    .eq(Messages::getId, messageId).update();
        }

        if (isReceive){
            messagesDto.setUsername(user.getUsername());
            userBaseService.lambdaQuery().select(UserBase::getUsername)
                    .eq(UserBase::getId, messages.getSendUserId()).oneOpt().ifPresent(userBase -> {
                        messagesDto.setSendUserName(userBase.getUsername());
                    });
            this.lambdaUpdate().set(Messages::getIsread, true)
                    .set(Messages::getReadTime, new Date())
                    .eq(Messages::getId, messageId).update();
        }else {
            messagesDto.setSendUserName(user.getUsername());
            userBaseService.lambdaQuery().select(UserBase::getUsername)
                    .eq(UserBase::getId, messages.getUserId()).oneOpt().ifPresent(userBase -> {
                        messagesDto.setUsername(userBase.getUsername());
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

    @Override
    public Boolean removeMessage(Integer messageId, String username,Boolean isReceive) {
        TransactionStatus transaction = transactionManager.getTransaction(TransactionUtils.getTransactionDefinition());
        try {
            UserBase user = userBaseService.lambdaQuery().select(UserBase::getId,UserBase::getUsername)
                    .eq(UserBase::getUsername, username).one();
            if (isReceive){
                lambdaUpdate().set(Messages::getReceiveDelete, true)
                        .eq(Messages::getId, messageId)
                        .eq(Messages::getUserId, user.getId())
                        .update();
            }else {
                lambdaUpdate().set(Messages::getSenderDelete, true)
                        .eq(Messages::getSendUserId, user.getId())
                        .eq(Messages::getId, messageId)
                        .update();
            }
            lambdaQuery().select(Messages::getSenderDelete,Messages::getReceiveDelete)
                    .eq(Messages::getId, messageId).oneOpt().ifPresent(e1->{
                        if (e1.getSenderDelete() && e1.getReceiveDelete())
                        {
                            removeById(messageId);
                        }
                    });
            transactionManager.commit(transaction);
        }catch (Exception ex){
            transactionManager.rollback(transaction);
            return false;
        }
        return true;
    }
}

