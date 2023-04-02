package xyz.chener.zp.datasharing.requestProcess.exec;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.PeAllParams;

import java.util.Map;

public class InJsExec extends AbstractChainExecute {

    public static final String PATAMS_KEY = "global_params";

    public static final String ERROR_KEY = "global_error";


    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof PeAllParams pap){
            if (pap.getInJsPe()!=null) {
                String js = pap.getInJsPe().getJs();
                String error = null;
                String paramJson = null;

                try( Context jsContext =  Context.newBuilder("js")
                        .allowHostAccess(HostAccess.ALL).build()) {
                    ObjectMapper om = new ObjectMapper();

                    Value bindings = jsContext.getBindings("js");
                    bindings.putMember(PATAMS_KEY, om.writeValueAsString(pap.getNormalParams()));
                    bindings.putMember(ERROR_KEY, "");
                    jsContext.eval("js", js);
                    paramJson = bindings.getMember(PATAMS_KEY).toString();
                    error = bindings.getMember(ERROR_KEY).toString();
                    if(StringUtils.hasText(paramJson)){
                        Map<String,String> params = om.readValue(paramJson, new TypeReference<Map<String, String>>() { });
                        pap.getNormalParams().clear();
                        pap.getNormalParams().putAll(params);
                    }
                }catch (Exception exception){
                    throw new Exception("js参数处理执行异常:",exception);
                }

                if(StringUtils.hasText(error)){
                    throw new Exception( error);
                }
            }
            return pap;
        }

        return null;
    }
}
