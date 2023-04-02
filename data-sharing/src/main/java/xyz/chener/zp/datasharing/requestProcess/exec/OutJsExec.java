package xyz.chener.zp.datasharing.requestProcess.exec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.PeAllParams;

import java.util.List;
import java.util.Map;

public class OutJsExec extends AbstractChainExecute {

    public static final String RESULT_KEY = "global_result";

    public static final String ERROR_KEY = "global_error";


    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof PeAllParams pap){
            if (pap.getOutJsPe()!=null){
                String js = pap.getOutJsPe().getJs();

                String error = null;
                String resultJson = null;

                try( Context jsContext =  Context.newBuilder("js")
                        .allowHostAccess(HostAccess.ALL).build()) {
                    ObjectMapper om = new ObjectMapper();
                    om.registerModule(new JavaTimeModule());

                    Value bindings = jsContext.getBindings("js");
                    bindings.putMember(RESULT_KEY, om.writeValueAsString(pap.getNormalParams()));
                    bindings.putMember(ERROR_KEY, "");
                    jsContext.eval("js", js);
                    resultJson = bindings.getMember(RESULT_KEY).toString();
                    error = bindings.getMember(ERROR_KEY).toString();
                    if(StringUtils.hasText(resultJson)){
                        Map<String,List<Map<String,Object>>> result = om.readValue(resultJson, new TypeReference<Map<String, List<Map<String,Object>>>>() { });
                        pap.getResult().clear();
                        pap.getResult().putAll(result);
                    }
                }catch (Exception exception){
                    throw new Exception("js结果处理执行异常:",exception);
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
