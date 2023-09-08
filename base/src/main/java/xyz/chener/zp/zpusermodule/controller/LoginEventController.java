package xyz.chener.zp.zpusermodule.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.dto.UserLoginEventRecordDto;
import xyz.chener.zp.zpusermodule.service.UserLoginEventRecordService;

/**
 * @Author: chenzp
 * @Date: 2023/02/27/17:09
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
public class LoginEventController {

    private final UserLoginEventRecordService userLoginEventRecordService;

    public LoginEventController(UserLoginEventRecordService userLoginEventRecordService) {
        this.userLoginEventRecordService = userLoginEventRecordService;
    }


    @GetMapping("/getLoginEventList")
    public PageInfo<UserLoginEventRecordDto> getList(@ModelAttribute UserLoginEventRecordDto dto
            , @RequestParam(defaultValue = "1") Integer page
            , @RequestParam(defaultValue = "10") Integer size)
    {
        return userLoginEventRecordService.getList(dto,page,size);
    }

}
