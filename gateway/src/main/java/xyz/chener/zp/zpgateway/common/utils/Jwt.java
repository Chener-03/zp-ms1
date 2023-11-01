package xyz.chener.zp.zpgateway.common.utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.chener.zp.zpgateway.common.config.CommonConfig;
import xyz.chener.zp.zpgateway.common.entity.LoginUserDetails;

import java.util.Objects;

/**
 * @Author: chenzp
 * @Date: 2023/01/12/16:56
 * @Email: chen@chener.xyz
 */

@Component
@Slf4j
public class Jwt {

    private final CommonConfig commonConfig;

    private static final String bearer = "Bearer ";

    public Jwt(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }


    public LoginUserDetails decode(String jwt)
    {
        try {
            jwt = jwt.substring(bearer.length());
            Algorithm algorithm = Algorithm.HMAC256(commonConfig.getJwt().getSalt());
            JWTVerifier verifier = com.auth0.jwt.JWT.require(algorithm)
                    .withIssuer("xyz.chener.zp")
                    .build();
            DecodedJWT res = verifier.verify(jwt);
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            LoginUserDetails loginUserDetails = om.readValue(res.getClaim("data").asString(), new TypeReference<LoginUserDetails>() {
            });
            Objects.requireNonNull(loginUserDetails,"荷载信息缺失");
            return loginUserDetails;
        }catch (Exception e)
        {
            return null;
        }
    }

}