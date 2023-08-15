package xyz.chener.zp.zpusermodule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * (User2fa)表实体类
 *
 * @author makejava
 * @since 2023-07-25 20:21:47
 */

@TableName("user_2fa")
public class User2fa extends Model<User2fa> {


    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String totpSecretKey;
    
    private String bakKey;


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

    public String getTotpSecretKey() {
        return totpSecretKey;
    }

    public void setTotpSecretKey(String totpSecretKey) {
        this.totpSecretKey = totpSecretKey;
    }

    public String getBakKey() {
        return bakKey;
    }

    public void setBakKey(String bakKey) {
        this.bakKey = bakKey;
    }


}

