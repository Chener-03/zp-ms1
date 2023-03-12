package xyz.chener.zp.zpusermodule.config.oplog.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * (OperateRecord)表实体类
 *
 * @author makejava
 * @since 2023-03-12 20:18:33
 */
@SuppressWarnings("serial")
public class OperateRecord extends Model<OperateRecord> {
    
    private Long id;
    
    private String opName;
    
    private Integer issuccess;
    //入参json数组
    private String paramsData;
    //出参json数组
    private String returnData;
    
    private String failReason;
    
    private Long opUserId;
    
    private Date opTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public Integer getIssuccess() {
        return issuccess;
    }

    public void setIssuccess(Integer issuccess) {
        this.issuccess = issuccess;
    }

    public String getParamsData() {
        return paramsData;
    }

    public void setParamsData(String paramsData) {
        this.paramsData = paramsData;
    }

    public String getReturnData() {
        return returnData;
    }

    public void setReturnData(String returnData) {
        this.returnData = returnData;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Long getOpUserId() {
        return opUserId;
    }

    public void setOpUserId(Long opUserId) {
        this.opUserId = opUserId;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    }

