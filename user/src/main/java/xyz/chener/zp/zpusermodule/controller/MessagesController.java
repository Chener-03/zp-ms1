package xyz.chener.zp.zpusermodule.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.UnifiedReturn;

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

}
