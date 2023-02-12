package xyz.chener.zp.zpusermodule.ws.coded;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import lombok.extern.slf4j.Slf4j;
import xyz.chener.zp.common.utils.GZipUtils;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessage;

import java.nio.ByteBuffer;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:35
 * @Email: chen@chener.xyz
 */
@Slf4j
public class WsEncoder implements Encoder.Binary<WsMessage> {
    @Override
    public ByteBuffer encode(WsMessage object) throws EncodeException {
        ObjectMapper om = new ObjectMapper();
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        try {
             String json = om.writeValueAsString(object);
             byte[] data = GZipUtils.compressJson(json);
             return ByteBuffer.wrap(data);
         }catch (Exception e) {
             log.error(e.getMessage());
         }
        try {
            String json = om.writeValueAsString(WsMessage.EMPTY_MESSAGE);
            byte[] data = GZipUtils.compressJson(json);
            return ByteBuffer.wrap(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
