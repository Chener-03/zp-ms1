package xyz.chener.zp.datasharing.requestProcess.exec;

import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.OutPe;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.PeAllParams;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class OutExec extends AbstractChainExecute {
    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof PeAllParams pap){
            List<OutPe.OutItem> outItems = pap.getOutPe().getOutItems();

            // 检查返回大小限制
            checkRetuenByteSizeLimit(pap.getDsRequestConfig().getByteReturnLimie(), pap.getResult());

            // 默认只处理第一个sql执行结果，其它sql执行结果到js脚本中处理
            List<Map<String, Object>> res0 = pap.getResult().get("res0");
            if (res0 != null)
            {
                List<Map<String, Object>> newres = new ArrayList<>();
                res0.forEach(e->{
                    Map<String, Object> newval = new HashMap<>();
                    outItems.forEach(item->{
                        if (item.getParamKey()==null)
                            return;
                        Object o = e.get(item.getParamKey());
                        o = processFormat(item, o);
                        o = processTransform(item, o);
                        newval.put(Optional.ofNullable(item.getShowKey()).orElse(item.getParamKey()), o);
                    });
                    newres.add(newval);
                });
                pap.getResult().put("res0", newres);
            }
            return pap;
        }
        return null;
    }

    private Object processTransform(OutPe.OutItem item, Object o) {
        // 处理转换
        if (o !=null){
            if (StringUtils.hasText(item.getTransformTypes()) && item.getTransformTypes().equalsIgnoreCase(OutPe.TRANSFORM_TYPE.BASE64ENCODE)){
                o = Base64.getEncoder().encodeToString(o.toString().getBytes());
            }
            if (StringUtils.hasText(item.getTransformTypes()) && item.getTransformTypes().equalsIgnoreCase(OutPe.TRANSFORM_TYPE.ANTIANAPHYLAXIS)){
                o = antianaphylaxis(o.toString());
            }
        }
        return o;
    }

    private Object processFormat(OutPe.OutItem item, Object o) {
        // 处理format
        if (o !=null){
            if (StringUtils.hasText(item.getFomatterType()) && item.getFomatterType().equalsIgnoreCase(OutPe.FORMAT_TYPE.TIME)) {
                SimpleDateFormat sdf = new SimpleDateFormat(item.getFomatterExp());
                o = sdf.format(coverToData(o));
            }
        }

        // 处理默认值
        if (o ==null && StringUtils.hasText(item.getDefaultValue())){
            o = item.getDefaultValue();
        }
        return o;
    }

    private void checkRetuenByteSizeLimit(String size,Object obj){
        long l = Long.parseLong(size);
        if (l<=0)
            return;
        long s1 = ObjectUtils.getSerializableObjectSize(obj);
        if (s1>l){
            throw new RuntimeException("返回数据量大小超过限制");
        }
    }

    private Date coverToData(Object obj){
        if (obj instanceof Date d)
            return d;
        if (obj instanceof LocalDateTime ldt)
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        if (obj instanceof Long l)
            return new Date(l);
        return null;
    }

    private String antianaphylaxis(String data) {
        if (StringUtils.hasText(data)) {
            int length = data.length();
            int start = length / 3;
            int end = length * 2 / 3;
            return data.substring(0, start) + "****" + data.substring(end);
        }
        return null;
    }

}
