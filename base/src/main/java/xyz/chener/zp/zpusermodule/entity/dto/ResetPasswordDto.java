package xyz.chener.zp.zpusermodule.entity.dto;

/**
 * @Author: chenzp
 * @Date: 2023/03/02/16:33
 * @Email: chen@chener.xyz
 */
public class ResetPasswordDto {
    private Boolean success;

    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
