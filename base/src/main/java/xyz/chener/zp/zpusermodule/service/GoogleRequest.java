package xyz.chener.zp.zpusermodule.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * @Author: chenzp
 * @Date: 2023/02/06/14:34
 * @Email: chen@chener.xyz
 */

@HttpExchange("https://recaptcha.net")
public interface GoogleRequest {

    @PostExchange("/recaptcha/api/siteverify")
    String verify(@RequestParam("secret") String secret, @RequestParam("response") String response);

}
