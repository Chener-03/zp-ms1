package xyz.chener.zp.zpusermodule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * (UserConfig)表实体类
 *
 * @author makejava
 * @since 2023-03-11 19:12:35
 */
@SuppressWarnings("serial")
public class UserConfig extends Model<UserConfig> {

    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    
    private String configJsonLayout;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getConfigJsonLayout() {
        return configJsonLayout;
    }

    public void setConfigJsonLayout(String configJsonLayout) {
        this.configJsonLayout = configJsonLayout;
    }
}

