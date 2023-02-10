package xyz.chener.zp.zpusermodule.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import lombok.extern.slf4j.Slf4j;
import xyz.chener.zp.common.utils.GZipUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:32
 * @Email: chen@chener.xyz
 */

@Slf4j
public class WsDecoder implements Decoder.Binary<WsMessage> {


    @Override
    public WsMessage decode(ByteBuffer bytes) throws DecodeException {
        ObjectMapper om = new ObjectMapper();
        try {
            String json = GZipUtils.uncompressJson(bytes.array());
            return om.readValue(json, WsMessage.class);
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return WsMessage.EMPTY_MESSAGE;
    }

    @Override
    public boolean willDecode(ByteBuffer bytes) {
        return true;
    }
}
