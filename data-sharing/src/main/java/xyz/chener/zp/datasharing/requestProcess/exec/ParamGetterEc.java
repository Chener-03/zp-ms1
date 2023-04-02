package xyz.chener.zp.datasharing.requestProcess.exec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;
import xyz.chener.zp.datasharing.requestProcess.entity.RequestMethodType;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.InPe;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.PeAllParams;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 赋予参数，没有的话走默认值，不做其它检查
 */

public class ParamGetterEc extends AbstractChainExecute {

    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof PeAllParams pap){
            if (pap.getRequest().getMethod().equalsIgnoreCase(RequestMethodType.POST)
                && pap.getDsRequestConfig().getParamType().equalsIgnoreCase(RequestMethodType.PARAM_TYPE.JSON)
            ) {
                try(ServletInputStream is = pap.getRequest().getInputStream()) {
                    String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    ObjectMapper om = new ObjectMapper();
                    Map<String, String> map = om.readValue(json, new TypeReference<Map<String, String>>() {
                    });
                    pap.setNormalParams(map);
                    pap.getInPe().getInItems().forEach(e->{
                        if (!pap.getNormalParams().containsKey(e.getParamKey())) {
                            pap.getNormalParams().put(e.getParamKey(), e.getDefaultValue());
                        }
                    });
                }catch (Exception exception){
                    throw new RuntimeException("JSON类型参数解析失败(注意:暂不支持嵌套对象类型),异常为: " + exception.getMessage());
                }
            }else {
                List<InPe.InItem> inItems = pap.getInPe().getInItems();
                inItems.forEach(e->{
                    String value = pap.getRequest().getParameter(e.getParamKey());
                    if (StringUtils.hasText(e.getDefaultValue()) && !StringUtils.hasText(value)){
                        value = e.getDefaultValue();
                    }
                    pap.getNormalParams().put(e.getParamKey(),value);
                });
            }
            return pap;
        }
        return null;
    }
}
