package xyz.chener.zp.datasharing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * (DsRequestProcessConfig)表实体类
 *
 * @author makejava
 * @since 2023-04-02 10:24:08
 */
@SuppressWarnings("serial")
public class DsRequestProcessConfig extends Model<DsRequestProcessConfig> {

    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer requestConfigId;

    /**
     * @see xyz.chener.zp.datasharing.enums.ProcessTypeEnum
     */
    private String type;
    //配置json
    private String configJson;
    //自定义js脚本
    private String diyJavascript;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRequestConfigId() {
        return requestConfigId;
    }

    public void setRequestConfigId(Integer requestConfigId) {
        this.requestConfigId = requestConfigId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public String getDiyJavascript() {
        return diyJavascript;
    }

    public void setDiyJavascript(String diyJavascript) {
        this.diyJavascript = diyJavascript;
    }


}

