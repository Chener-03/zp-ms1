package xyz.chener.zp.zpusermodule.controller;

import com.github.pagehelper.PageInfo;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.zpusermodule.entity.dto.MessagesDto;
import xyz.chener.zp.zpusermodule.service.MessagesService;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/06/16:34
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class MessagesController {

    private final MessagesService messagesService;

    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @GetMapping("/getUserMessageById")
    public MessagesDto getUserMessageById(@RequestParam(value = "messageId") Integer messageId
            , @RequestParam(value = "isReceive") Boolean isReceive){
        return messagesService.getUserMessageById(SecurityContextHolder.getContext().getAuthentication().getName()
                ,messageId,isReceive);
    }


    @PostMapping("/sendUsersMessage")
    public Boolean sendUsersMessage(@ModelAttribute @Validated MessagesDto messagesDto
            , @RequestParam("userIds") @Size(min=1,message = "用户不能为空") List<Long> userIds)
    {
        return messagesService.sendUsersMessage(messagesDto,userIds,SecurityContextHolder.getContext().getAuthentication().getName());
    }


    @GetMapping("/getMessagesList")
    public PageInfo<MessagesDto> getMessagesList(@ModelAttribute MessagesDto messagesDto
            , @RequestParam(value = "isReceive") Boolean isReceive
            , @RequestParam(value = "page",defaultValue = "1") Integer page, @RequestParam(value = "size",defaultValue = "10") Integer size){
        return messagesService.getMessagesList(messagesDto,SecurityContextHolder.getContext().getAuthentication().getName()
                ,isReceive,page,size);
    }

    @PostMapping("/removeMessage")
    public Boolean removeMessage(@RequestParam("messageId") Integer messageId,@RequestParam(value = "isReceive") Boolean isReceive){
        return messagesService.removeMessage(messageId,SecurityContextHolder.getContext().getAuthentication().getName(), isReceive);
    }

}
