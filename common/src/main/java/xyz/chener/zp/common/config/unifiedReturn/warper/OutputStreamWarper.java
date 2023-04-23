package xyz.chener.zp.common.config.unifiedReturn.warper;

import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author: chenzp
 * @Date: 2023/04/23/09:48
 * @Email: chen@chener.xyz
 * @Description: 直接返回输出流包装器
 */

public interface OutputStreamWarper {
    default InputStream getInputStream(){
        return null;
    }

    default void setResponse(HttpServletResponse response){
        // TODO nothing
    }

}
