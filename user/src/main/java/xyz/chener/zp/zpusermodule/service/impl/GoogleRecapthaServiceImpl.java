package xyz.chener.zp.zpusermodule.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.chener.zp.zpusermodule.entity.dto.GoogleRecaptchaResponse;
import xyz.chener.zp.zpusermodule.service.GoogleRecapthaService;
import xyz.chener.zp.zpusermodule.service.GoogleRequest;

/**
 * @Author: chenzp
 * @Date: 2023/02/06/14:32
 * @Email: chen@chener.xyz
 */

@Service
public class GoogleRecapthaServiceImpl implements GoogleRecapthaService
{

    private final GoogleRequest googleRequest;

    public GoogleRecapthaServiceImpl(GoogleRequest googleRequest) {
        this.googleRequest = googleRequest;
    }

    @Override
    public boolean check(String response) {
        if (!StringUtils.hasText(response)){
            return false;
        }

        try {
            String body = googleRequest.verify(GoogleRecapthaService.secret, response);
            GoogleRecaptchaResponse resp = new ObjectMapper().readValue(body, GoogleRecaptchaResponse.class);
            return resp.getSuccess() && resp.getScore() > GoogleRecapthaService.score;
        }catch (Exception ignored){ }
        return false;
    }
}
