package com.gome.meidian.account.mobileverificationcode;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class ChangePasswordVo implements Serializable {


    @NotBlank(message = "{work.user.changepwd.oldpwd.isNull}")
    private String oldPwd;
    @NotBlank(message = "{work.user.changepwd.newpwd.isNull}")
    @CustomPattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[\\S]{6,20}$", message = "{work.user.changepwd.validatePassword.error}")
    private String newPwd;
    @NotBlank(message = "{work.user.changepwd.verifypwd.isNull}")
    @CustomPattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[\\S]{6,20}$", message = "{work.user.changepwd.validatePassword.error}")
    private String verifypwd;


    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getVerifypwd() {
        return verifypwd;
    }

    public void setVerifypwd(String verifypwd) {
        this.verifypwd = verifypwd;
    }
}
