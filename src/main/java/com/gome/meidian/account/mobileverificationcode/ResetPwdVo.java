package com.gome.meidian.account.mobileverificationcode;

import org.hibernate.validator.constraints.NotBlank;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @description: 找回密码
 * @company：美信企业办公部
 * @author: skyler
 * @time: 2016年9月23日 下午6:07:39
 */
public class ResetPwdVo implements Serializable {

    /**
	 * serialVersionUID: TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = "{work.user.validatePassword.isNull}")
    @CustomPattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[\\S]{6,20}$", message = "{work.user.changepwd.validatePassword.error}")
    private String password;
	
	@NotNull(message = "{work.user.validatePassword.isNull}")
    @CustomPattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[\\S]{6,20}$", message = "{work.user.changepwd.validatePassword.error}")
    private String verifyPassword;
    
	@NotNull(message = "{work.user.validate_uuid.isNull}")
	@Size(min = 1, message = "{work.user.validate_uuid.isNull}")
    private String uuid;
    
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVerifyPassword() {
		return verifyPassword;
	}
	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
