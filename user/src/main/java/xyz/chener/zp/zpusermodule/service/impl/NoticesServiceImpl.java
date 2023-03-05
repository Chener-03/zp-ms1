package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.common.utils.TransactionUtils;
import xyz.chener.zp.zpusermodule.dao.NoticesDao;
import xyz.chener.zp.zpusermodule.entity.*;
import xyz.chener.zp.zpusermodule.entity.dto.NoticesDto;
import xyz.chener.zp.zpusermodule.service.NoticesService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.ws.WsMessagePublisher;
import xyz.chener.zp.zpusermodule.ws.mq.entity.NotifyMessage;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * (Notices)表服务实现类
 *
 * @author makejava
 * @since 2023-03-04 18:21:14
 */
@Service
public class NoticesServiceImpl extends ServiceImpl<NoticesDao, Notices> implements NoticesService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(NoticesServiceImpl.class);

    private final WsMessagePublisher wsMessagePublisher;

    private final DataSourceTransactionManager transactionManager;
    private final UserBaseServiceImpl userBaseService;
    private final MessagesServiceImpl messagesService;

    public NoticesServiceImpl(WsMessagePublisher wsMessagePublisher, DataSourceTransactionManager transactionManager, UserBaseServiceImpl userBaseService, MessagesServiceImpl messagesService) {
        this.wsMessagePublisher = wsMessagePublisher;
        this.transactionManager = transactionManager;
        this.userBaseService = userBaseService;
        this.messagesService = messagesService;
    }

    @Override
    public Boolean publish(NoticesDto dto, List<String> userNames, List<String> ditchs) {
        Boolean res = false;
        TransactionDefinition td = TransactionUtils.getTransactionDefinition();
        TransactionStatus transaction = transactionManager.getTransaction(td);
        try{
            Notices ntc = new Notices();
            ObjectUtils.copyFields(dto,ntc);
            if (ditchs != null){
                ntc.setDitch(String.join(",",ditchs));
            }
            if (userNames!=null){
                ntc.setUsers(String.join(",",userNames));
            }
            ntc.setCreateTime(new Date());
            UserBase u = userBaseService.lambdaQuery().select(UserBase::getId).eq(UserBase::getUsername, SecurityContextHolder.getContext().getAuthentication().getName()).one();
            ntc.setPublishUserId(u.getId());
            this.save(ntc);
            switch (dto.getType()){
                case NoticeTypeEnum.ALL_USER ->{
                    publishAllUser(dto,ditchs,u.getId());
                }
                case NoticeTypeEnum.PART_USER ->{
                    publishUserWithUserIds(userNames,dto,ditchs,u.getId());
                }
                case NoticeTypeEnum.ONLINE_USER ->{
                    List<String> allOnlineUser = userBaseService.getAllWsOnlineUsersName();
                    publishUserWithUsername(allOnlineUser,dto,ditchs,u.getId());
                }
            }
            transactionManager.commit(transaction);
            res = true;
        }catch (Exception e){
            res = false;
            transactionManager.rollback(transaction);
        }
        return res;
    }


    private void publishAllUser( NoticesDto dto,List<String> ditchs,Long publishUserId ){
        List<UserBase> list = userBaseService.lambdaQuery().select(UserBase::getId, UserBase::getUsername).list();
        if (ditchs.contains(NoticeDitchEnum.WS))
        {
            publishAllUserWithWS(dto);
        }
        CopyOnWriteArrayList<Messages> messages = new CopyOnWriteArrayList<>();
        list.parallelStream().forEach(e->{
            if (ditchs.contains(NoticeDitchEnum.INSIDE)){
                Messages msg = new Messages();
                ObjectUtils.copyFields(dto,msg);
                msg.setUserId(e.getId());
                msg.setType(Messages.Type.TEXT);
                msg.setImp(Messages.Imp.NORMAL);
                msg.setCreateTime(new Date());
                msg.setIsread(false);
                msg.setSendUserId(publishUserId);
                messages.add(msg);
            }
            // todo: add email phone
            if (ditchs.contains(NoticeDitchEnum.EMAIL)){
                logger.info("send email[{}] to {}",dto.getContent(),e.getUsername());
            }
            if (ditchs.contains(NoticeDitchEnum.PHONE)){
                logger.info("send phone[{}] to {}",dto.getContent(),e.getUsername());
            }
        });
        if (messages.size()>0){
            messagesService.saveBatch(messages);
        }

    }


    private void publishUserWithUserIds(List<String> usersId, NoticesDto dto, List<String> ditchs, Long publishUserId){
        CopyOnWriteArrayList<Messages> messages = new CopyOnWriteArrayList<>();
        usersId.parallelStream().forEach(e->{
            UserBase user = userBaseService.lambdaQuery()
                    .select(UserBase::getId,UserBase::getUsername).eq(UserBase::getId, e).one();
            if (user != null){
                if (ditchs.contains(NoticeDitchEnum.WS)){
                    publishOneUserWithWS(dto,user.getUsername());
                }
                if (ditchs.contains(NoticeDitchEnum.INSIDE)){
                    Messages msg = new Messages();
                    ObjectUtils.copyFields(dto,msg);
                    msg.setUserId(user.getId());
                    msg.setType(Messages.Type.TEXT);
                    msg.setImp(Messages.Imp.NORMAL);
                    msg.setCreateTime(new Date());
                    msg.setIsread(false);
                    msg.setSendUserId(publishUserId);
                    messages.add(msg);
                }
                // todo: add email phone
                if (ditchs.contains(NoticeDitchEnum.EMAIL)){
                    logger.info("send email[{}] to {}",dto.getContent(),user.getUsername());
                }
                if (ditchs.contains(NoticeDitchEnum.PHONE)){
                    logger.info("send phone[{}] to {}",dto.getContent(),user.getUsername());
                }
            }

        });
        if (messages.size()>0){
            messagesService.saveBatch(messages);
        }
    }

    private void publishUserWithUsername( List<String> userNames,NoticesDto dto,List<String> ditchs,Long publishUserId){
        CopyOnWriteArrayList<Messages> messages = new CopyOnWriteArrayList<>();
        userNames.parallelStream().forEach(e->{
            UserBase user = userBaseService.lambdaQuery()
                    .select(UserBase::getId,UserBase::getUsername).eq(UserBase::getUsername, e).one();
            if (user != null){
                if (ditchs.contains(NoticeDitchEnum.WS)){
                    publishOneUserWithWS(dto,user.getUsername());
                }
                if (ditchs.contains(NoticeDitchEnum.INSIDE)){
                    Messages msg = new Messages();
                    ObjectUtils.copyFields(dto,msg);
                    msg.setUserId(user.getId());
                    msg.setType(Messages.Type.TEXT);
                    msg.setImp(Messages.Imp.NORMAL);
                    msg.setCreateTime(new Date());
                    msg.setIsread(false);
                    msg.setSendUserId(publishUserId);
                    messages.add(msg);
                }
                // todo: add email phone
                if (ditchs.contains(NoticeDitchEnum.EMAIL)){
                    logger.info("send email[{}] to {}",dto.getContent(),user.getUsername());
                }
                if (ditchs.contains(NoticeDitchEnum.PHONE)){
                    logger.info("send phone[{}] to {}",dto.getContent(),user.getUsername());
                }
            }

        });
        if (messages.size()>0){
            messagesService.saveBatch(messages);
        }
    }


    private Boolean publishOneUserWithWS(NoticesDto dto,String username){
        NotifyMessage message = new NotifyMessage();
        message.setType(NotifyMessage.TYPE.ONE_USER);
        message.setUser(username);
        message.setTitle("通知");
        message.setContent(dto.getContent());
        wsMessagePublisher.publishWsUserMessage(message);
        return true;
    }

    private Boolean publishAllUserWithWS(NoticesDto dto){
        NotifyMessage message = new NotifyMessage();
        message.setType(NotifyMessage.TYPE.ALL_USER);
        message.setTitle("通知");
        message.setContent(dto.getContent());
        wsMessagePublisher.publishWsUserMessage(message);
        return true;
    }

}

