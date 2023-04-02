package xyz.chener.zp.datasharing.requestProcess.exec;

import org.springframework.util.StringUtils;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;
import xyz.chener.zp.datasharing.requestProcess.entity.RequestMethodType;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.InPe;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.PeAllParams;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class InExec extends AbstractChainExecute {
    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof PeAllParams pap){
            mustCheck(pap);
            transform(pap);
            validate(pap);
            checkParamSize(pap);
            return pap;
        }
        return null;
    }


    private void mustCheck(PeAllParams pap) throws Exception{
        List<InPe.InItem> inItems = pap.getInPe().getInItems();
        for (InPe.InItem item : inItems) {
            if (item.isMust()) {
                if (pap.getNormalParams().get(item.getParamKey())==null) {
                    throw new Exception("必须参数" + item.getParamKey() + "未传入");
                }
            }
        }
    }

    private void transform(PeAllParams pap) throws Exception{
        List<InPe.InItem> inItems = pap.getInPe().getInItems();
        for (InPe.InItem e : inItems) {
            if (StringUtils.hasText(e.getTransformTypes())) {
                if (e.getTransformTypes().contains(InPe.TRANSFORM_TYPE.BASE64DECODE)){
                    String v = pap.getNormalParams().get(e.getParamKey());
                    if (v!=null){
                        try {
                            pap.getNormalParams().put(e.getParamKey(),new String(Base64.getDecoder().decode(v), StandardCharsets.UTF_8));
                        }catch (Exception exception) {
                            throw new Exception("参数" + e.getParamKey() + "BASE64解码失败,请检查是否为BASE64编码");
                        }
                    }
                }
            }


        }
    }

    private void validate(PeAllParams pap) throws Exception{
        List<InPe.InItem> inItems = pap.getInPe().getInItems();
        for (InPe.InItem item : inItems) {
            String verifyTypes = item.getVerifyTypes();
            if (!StringUtils.hasText(verifyTypes)) {
                continue;
            }

            if(verifyTypes.startsWith(InPe.VERIFY_TYPE.REGULAR)){
                String regular = verifyTypes.substring(InPe.VERIFY_TYPE.REGULAR.length());
                String v = pap.getNormalParams().get(item.getParamKey());
                if (v!=null){
                    if (!v.matches(regular)){
                        throw new Exception("参数" + item.getParamKey() + "不符合正则表达式" + regular);
                    }
                }
                continue;
            }

            if (verifyTypes.contains(InPe.VERIFY_TYPE.NUMBER)){
                String v = pap.getNormalParams().get(item.getParamKey());
                try {
                    Double.parseDouble(v);
                }catch (Exception exception){
                    throw new Exception("参数" + item.getParamKey() + "不是数字");
                }
            }

            if (verifyTypes.contains(InPe.VERIFY_TYPE.STRING)){
                String v = pap.getNormalParams().get(item.getParamKey());
                if (v!=null){
                    if (!v.matches("[a-zA-Z0-9_]+")){
                        throw new Exception("参数" + item.getParamKey() + "不是字符串");
                    }
                }
            }

            if (verifyTypes.contains(InPe.VERIFY_TYPE.SQL_INJECT)){
                String v = pap.getNormalParams().get(item.getParamKey());
                if (v!=null){
                    if (v.matches("\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|'|%)")){
                        throw new Exception("参数" + item.getParamKey() + "包含非法字符");
                    }
                }
            }
        }
    }


    private void checkParamSize(PeAllParams pap) throws Exception {

        long l = Long.parseLong(pap.getDsRequestConfig().getByteReqLimit());
        if (l == 0) {
            return;
        }
        long size = ObjectUtils.getSerializableObjectSize(pap.getNormalParams());
        if (size > l) {
            throw new Exception(String.format("请求体过大,超过限制: %s/%s",size,l));
        }

    }

}
