package com.gome.meidian.account.shiro;

/**
 * @author qupeng
 */
public enum StatusCode {
	// TODO 待完善，需要和前端协商
	SUCCESS(0, "OK"),
	// 非法操作
	ILLEGAL_OPERATION(10001, "illegal operation"),
	// 系统错误，所有为止错误均返回此状态码
	SYSTEM_ERROR(10500, "System error"),
	// request not found
	NOT_FOUND(10404, "Not found"),
	// 重定向请求
	REQUEST_REDIRECT(10302, "Redirect"),
	// 校验字段信息错误
	VALIDATION_FAILED(10999,"Volidate failed"),
	/*********************************/

	// 参数类型匹配错误
	TYPE_MISMATH(11001, "Parameter type mismath:[%s]"),
	// 缺少必要参数
	MISSING_REQUEST_PARAMETER(11002, "Missing request parameter:[%s]"),
	// 参考HttpMessageNotReadableException 请求错误
	HTTP_MESSAGE_NOT_READABLE(11003, "Check parameter"),

	/*********************************/
	//登录失败
	LOGIN_ERROR(12001,"手机号与密码不匹配，请重新输入！"),
	//帐号不存在
	LOGIN_ACCOUNT_NOT_EXIST(12002,"手机号码不存在，请重新输入"),
	//帐号异常
	ABNORMAL_ACCOUNT(12003,"Login error; Abnormal account"),
	//帐号被停用
	DISABLED_ACCOUNT(12004,"该账号已停用，请联系管理员"),

	// 未登录
	NOT_LOGGED_ON(12011, "Not logged on"),
	// 没有权限
	NO_PERMISSION(12012, "No permission"),
	// session 失效
	SID_EXPIRED(12013, "Session expired");

	/**
	 * 错误代码
	 */
	private int code;
	/**
	 * 简短描述
	 */
	private String msg;

	private StatusCode(int code, String msg) {

		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return this.code;
	}

	public String getMsg() {
		return this.msg;
	}

}
