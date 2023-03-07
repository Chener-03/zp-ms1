package xyz.chener.zp.zpusermodule.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.dto.MessagesDto;
import xyz.chener.zp.zpusermodule.service.MessagesService;

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
    public MessagesDto getUserMessageById(@RequestParam Integer messageId){
        return messagesService.getUserMessageById(SecurityContextHolder.getContext().getAuthentication().getName()
                ,messageId);
    }

}
