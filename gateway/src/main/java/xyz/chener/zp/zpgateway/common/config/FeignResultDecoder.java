package xyz.chener.zp.zpgateway.common.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import xyz.chener.zp.zpgateway.common.entity.R;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;


@Slf4j
public class FeignResultDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {

        try {
            String body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            R<?> obj = om.readValue(body, TypeFactory.defaultInstance()
                    .constructFromCanonical(String.format("%s<%s>", R.class.getName(), type.getTypeName())));
            if (obj.getCode()!=R.HttpCode.HTTP_OK.get())
            {
                log.warn("openfeign call error : "+obj.getMessage());
            }
            return obj.getObj();

        }catch (Exception exception)
        {
            exception.printStackTrace();
            log.error(exception.getMessage());
            throw new RuntimeException(R.ErrorMessage.FEIGN_DECODER_ERROR.get());
        }
    }
}
