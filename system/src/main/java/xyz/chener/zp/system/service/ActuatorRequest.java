package xyz.chener.zp.system.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import xyz.chener.zp.common.entity.CommonVar;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/17/15:35
 * @Email: chen@chener.xyz
 */

@HttpExchange("http://none")
public interface ActuatorRequest {

    String prefix = "/actuator";

    @GetExchange(prefix+"/health")
    String getHealthBaseInfo(@RequestHeader(CommonVar.OPEN_FEIGN_HEADER) String headerKey);

}
