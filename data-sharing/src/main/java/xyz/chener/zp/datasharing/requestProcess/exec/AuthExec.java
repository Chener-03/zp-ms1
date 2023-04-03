package xyz.chener.zp.datasharing.requestProcess.exec;

import org.springframework.util.StringUtils;
import xyz.chener.zp.common.utils.Md5Utiles;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.common.utils.RequestUtils;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.AuthPe;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.PeAllParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthExec extends AbstractChainExecute {
    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof PeAllParams pap){
            if (pap.getAuthPe() != null){
                List<AuthPe.AuthItem> authItems = pap.getAuthPe().getAuthItems();
                for (AuthPe.AuthItem authItem : authItems) {
                    if (StringUtils.hasText(authItem.getAuthType())){
                        // md5验证
                        if (authItem.getAuthType().contains(AuthPe.MD5)){
                            StringBuilder sb = new StringBuilder();
                            authItem.getParamKeys().forEach(e->{
                                if (pap.getNormalParams().get(e)!=null){
                                    sb.append(pap.getNormalParams().get(e));
                                }
                            });
                            if (StringUtils.hasText(authItem.getMd5Slat())){
                                sb.append(authItem.getMd5Slat());
                            }

                            String strMd5 = Md5Utiles.getStrMd5(sb.toString());
                            if (!ObjectUtils.nullSafeEquals(strMd5,pap.getNormalParams().get(authItem.getMd5ParamKey()))) {
                                throw new Exception("权限验证失败,请检查MD5传参是否正确.");
                            }
                        }

                        if (authItem.getAuthType().contains(AuthPe.IP)){
                            String sourceIp = RequestUtils.getConcurrentIp();
                            boolean res = false;
                            for (String ip : authItem.getIps()) {
                                if (matchIp(sourceIp,ip)){
                                    res = true;
                                    break;
                                }
                            }
                            if (!res){
                                throw new Exception("权限验证失败,请检查IP是否允许.");
                            }
                        }

                        if (authItem.getAuthType().contains(AuthPe.HEAD)){
                            String heads = authItem.getHeads();
                            Map<String, String> map = parseKeyValueString(heads);
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                if (!ObjectUtils.nullSafeEquals(value,pap.getRequest().getHeader(key))){
                                    throw new Exception("权限验证失败,请检查请求头是否正确.");
                                }
                            }
                        }

                    }
                }
            }
            return pap;
        }
        return null;
    }


    public static boolean matchIp(String ip, String pattern) {
        String[] ipFields = ip.split("\\.");
        String[] patternFields = pattern.split("\\.");
        if (ipFields.length != patternFields.length) {
            return false;
        }
        for (int i = 0; i < ipFields.length; i++) {
            String ipField = ipFields[i];
            String patternField = patternFields[i];
            if (patternField.equals("*")) {
                continue;
            }
            if (!ipField.equals(patternField)) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, String> parseKeyValueString(String input) {
        Map<String, String> result = new HashMap<>();

        if (input == null || input.isEmpty()) {
            return result;
        }

        String[] pairs = input.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid key-value pair: " + pair);
            }

            String key = keyValue[0];
            String value = keyValue[1];
            result.put(key, value);
        }

        return result;
    }



}
