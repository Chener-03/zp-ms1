package xyz.chener.zp.datasharing;


import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration;
import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.aviator.AviatorEvaluator;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import xyz.chener.zp.common.utils.MapBuilder;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.datasharing.config.DataSharingSourceConfig;
import xyz.chener.zp.datasharing.entity.thirdparty.UserBase;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.*;
import xyz.chener.zp.datasharing.requestProcess.exec.AuthExec;
import xyz.chener.zp.datasharing.requestProcess.exec.InJsExec;
import xyz.chener.zp.datasharing.requestProcess.exec.SqlExec;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication(exclude = SentinelEndpointAutoConfiguration.class)
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableConfigurationProperties({DataSharingSourceConfig.class})
public class DatasharingApplication {
    public static void main(String[] args) throws Exception {
        ObjectMapper om = new ObjectMapper();
        InPe inPe = new InPe();
        InPe.InItem i1 = new InPe.InItem();
        i1.setParamKey("p1");
        i1.setDefaultValue("default");
        i1.setTransformTypes(InPe.TRANSFORM_TYPE.BASE64DECODE);
        i1.setVerifyTypes(InPe.VERIFY_TYPE.NUMBER+","+InPe.VERIFY_TYPE.SQL_INJECT);
        i1.setMust(false);
        inPe.getInItems().add(i1);

        InPe.InItem i2 = new InPe.InItem();
        i2.setParamKey("p2");
        i2.setDefaultValue("defaultp2");
        i2.setMust(true);
        inPe.getInItems().add(i2);

        String s = om.writerWithDefaultPrettyPrinter().writeValueAsString(inPe);

        SqlPe sqlPe = new SqlPe();
        SqlPe.SQL_ENTITY see = new SqlPe.SQL_ENTITY();
        see.setType(SqlPe.TYPE_SELECT);
        see.setSql("SELECT * FROM user_base WHERE id = '${p1}' and username = '${p2}'");
        sqlPe.setDataSourceId(7);
        sqlPe.getSqls().add(see);
        om.writerWithDefaultPrettyPrinter().writeValueAsString(sqlPe);

        OutPe outPe = new OutPe();
        OutPe.OutItem oi = new OutPe.OutItem();
        oi.setParamKey("create_time");
        oi.setFomatterType(OutPe.FORMAT_TYPE.TIME);
        oi.setFomatterExp("yyyy-MM-dd HH:mm:ss");
        oi.setDefaultValue("2020-01-01 00:00:00");
        oi.setShowKey("CreateTime");
        outPe.getOutItems().add(oi);

        OutPe.OutItem oi2 = new OutPe.OutItem();
        oi2.setParamKey("id");
        oi2.setShowKey("Id");
        outPe.getOutItems().add(oi2);

        OutPe.OutItem oi3 = new OutPe.OutItem();
        oi3.setParamKey("fuck");
        oi3.setShowKey("FUCK");
        oi3.setDefaultValue("fuckdefault");
        outPe.getOutItems().add(oi3);

        om.writerWithDefaultPrettyPrinter().writeValueAsString(outPe);

        OutDataPe outDataPe = new OutDataPe();
        outDataPe.setType(OutDataPe.TYPE.JSON);
        om.writerWithDefaultPrettyPrinter().writeValueAsString(outDataPe);

        System.setProperty("csp.sentinel.log.output.type","console");
        System.setProperty("polyglot.engine.WarnInterpreterOnly","false");
        SpringApplication.run(DatasharingApplication.class, args);
    }
}
