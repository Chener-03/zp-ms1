package xyz.chener.zp.zpusermodule.ws;

import jakarta.websocket.server.ServerEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

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
}
