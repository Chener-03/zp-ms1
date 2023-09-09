package xyz.chener.zp.zpusermodule.ws;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import xyz.chener.zp.common.entity.CommonVar;
import xyz.chener.zp.common.utils.IpUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:17
 * @Email: chen@chener.xyz
 */

@Configuration
public class WsConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }



    public static class WsConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            Map<String, Object> secUserProperties = sec.getUserProperties();
            secUserProperties.put(CommonVar.IP_HEAD,getIp(request));
            super.modifyHandshake(sec, request, response);
        }

        private String getIp(HandshakeRequest request){
            List<String> ips = request.getHeaders().get(CommonVar.IP_HEAD);
            if (ips == null || ips.isEmpty()){
                return IpUtils.getRealIp();
            }
            return ips.get(0);
        }
    }
}
