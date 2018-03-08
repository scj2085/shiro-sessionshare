package com.gome.meidian.account.shiro;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 常量类
 *
 * @author qupeng
 *
 */
public class Constants {


	// session 失效时间12小时
	public static final long SESSION_EXPIRE_TIME = (long) TimeUnit.HOURS.toMillis(12);
	// app token 失效时间 30天
	public static final long APP_SESSION_EXPIRE_TIME = TimeUnit.HOURS.toMillis(24*30);
	// user session in redis 失效时间 30天
	public static int REDIS_DEFAULT_EXPIRE_TIME = (int) TimeUnit.MILLISECONDS.toSeconds(Constants.APP_SESSION_EXPIRE_TIME);
	// 记住我失效时间 5天
	public static final int REMEMBER_ME_EXPIRE_TIME = (int) TimeUnit.HOURS.toSeconds(24*5);
	//redis session user map
	public static final byte[] SESSION_USER_KEY = "session_user_map".getBytes();
	//redis session key
	public static final String SHIRO_REDIS_SESSION = "shiro_redis_session:";
	//redis cache key
	public static final String SHIRO_REDIS_CACHE = "shiro_redis_cache:";
	//redis user cache key
	public static final String REDIS_USER_CACHE = "redis_user_cache:";
	//员工修改密码标识
	public static final String USER_CHANGE_PASSWORD = "redis_user_password_cache:";

	// 修改密码监听时间30天
	public static final int USER_CHANGE_PWD_EXPIRE_TIME = (int) TimeUnit.HOURS.toSeconds(24 * 30);
	// session attribute : delete staff session key
	public static final String DELETE_STAFF_SESSION_KEY = "DELETE_STAFF_SESSION_KEYS:";
	// session attribute for kick
	public static final String KICK_APP_SESSION_KEY = "KICK_APP_SESSION_KEY";

	// 逻辑删除状态
	public final static int DEL_STATUS = 1;
	// client type
	public final static String CLIENT_OS_HEADER = "clientOs";
	// phone type
	public final static String PHONE_TYPE_HEADER = "phoneType";
	// device id
	public final static String DEV_ID_HEADER = "devId";
	// app version
	public final static String APP_VERSION_HEADER = "appVersion";
	// ios device
	public final static String IOS_CLIENT = "1";
	// android device
	public final static String ANDROID_CLIENT = "2";
	// web device
	public final static String WEB_CLIENT = "3";
	// h5 device
	public final static String H5_CLIENT = "4";
	// mac device
	public final static String MAC_CLIENT = "5";
	// windows device
	public final static String WINDOWS_CLIENT = "6";
	// other device
	public final static String OTHER = "other";

	//shiro 随机密码
	public static final String SHIRO_PASSWORD = "meipro";

	//码位数
	public static final int CODE_DIGIT = 6;
	public static final int MIN_DIGIT = 6;
	public static final int MAX_DIGIT = 20;

	//指定日期的指定天
	public static final int ADD_DAYS = 3;

	//redis 记录发送短信验证码和发送时间
	public static final String REDIS_SEND_MSG_KEY_PREFIX = "send_msg:";

	//redis 记录发送短信验证码和发送时间
	public static final String REDIS_SEND_SMS_KEY_PREFIX = "send_sms:";

	//redis 记录创建公司时发送短信验证码和发送时间
	public static final String REDIS_SEND_REGISTER_SMS_KEY_PREFIX = "send_register_sms:";

	//redis 记录每天发送短信次数限制
	public static final String REDIS_SEND_SMS_COUNT_KEY_PREFIX = "send_sms_count:";

	//redis 记录创建公司时每天发送短信次数限制
	public static final String REDIS_SEND_REGISTER_SMS_COUNT_KEY_PREFIX = "send_register_sms_count:";

	//redis 记录发送图形验证码
	public static final String REDIS_SEND_PIC_KEY_PREFIX = "send_pic:";

	//redis 记录未激活员工邀请次数
	public static final String REDIS_ADMIN_INVITE_STAFF_PREFIX = "admin_invite_staff:";

	//redis 记录未激活员工邀请次数
	public static final String REDIS_ADMIN_INVITE_STAFFS_PREFIX = "admin_invite_staffs:";

	//图形验证码失效时间 30分钟：秒级
	public static final long PICTURE_CAPTCHA_EXPIRE_TIME = TimeUnit.MINUTES.toSeconds(30);

	//短信验证码、uuid失效时间 15分钟：秒级
	public static final int MSG_SESSION_EXPIRE_TIME = (int)TimeUnit.MINUTES.toSeconds(15);

	//每人每天邀请员工次数
	public static final String ADMIN_INVITE_COUNT_1 = "1";
	//每人每天邀请员工次数
	public static final String ADMIN_INVITE_COUNT_2 = "2";
	//群组二维码有效时间 7天
	public static final int QR_CODE_EXPIRE_TIME = (int) TimeUnit.HOURS.toSeconds(24 * 7);
	//redis qrcode key
	public static final String QR_CODE_KEY = "redis_qr_code_key:";

	//缓存exccel上传正确数据key
	public static final String EXCEL_DATA = "redis_excel_data_key:";
	//缓存exccel上传正确数据时间3天
	public static final int EXCEL_DATA_EXPIRE_TIME = (int) TimeUnit.HOURS.toSeconds(24 * 3);

	//管理员权限标识
	public static final int ADMIN_PERMISSION_FLAG = 1;

	//员工权限标识
	public static final int WORK_PERMISSION_FLAG = 2;

	//系统管理员角色代码
	public static final String SYSTEM_ADMIN_ROLE_CODE = "system-admin";

	//虚拟管理员角色
	public static final String VIRTUAL_ADMIN_ROLE_CODE = "admin";

    //不可更改的权限Id
    public static final Long[] INVARIABLE_PERMISSION_IDS = {1L};

	//IM错误码
	public static final int IM_ERROR_CODE = 1;

	//系统模块默认code
	public static final String DEFAULT_MODULE_CODE = "0000";

	public static final int CHANNEL_STATUS_CREATE = 0;
	public static final int CHANNEL_STATUS_DEL = 1;
	public static final int CHANNEL_STATUS_UPDATE = 2;
	public static final int CHANNEL_STATUS_ARCHIVE = 3;

	public static final String CHANNEL_ID_PREIX = "channelId:";
	public static final String ALLCHANNEL_PREIX = "allchannelId:";
	
	//是否接受普通消息 0:接收 1:不接收
	public static final int RECEIVE_COMMON_MSGYES = 0;//0:接收
	public static final int RECEIVE_COMMON_MSGNO = 1;//1:不接收

	public static final int LIST_TYPE_COMMON = 0; //普通员工
	public static final int LIST_TYPE_SPECIAL= 1; //特邀成员
	public static final int LIST_TYPE_BLACK = 2; //黑名单
	
	public static byte[] getSessionRawKey(Serializable sessionId) {
		return (Constants.SHIRO_REDIS_SESSION + sessionId).getBytes();
	}

	public static String getAuthorizationCacheKey(Serializable staffId) {
		return Constants.SHIRO_REDIS_CACHE + staffId;
	}

	public static byte[] getRawKey(Object key) {
		if (key instanceof String) {
			return getSessionRawKey((String) key);
		} else if (key instanceof Long) {
			return getAuthorizationCacheKey((Long) key).getBytes();
		} else {
			throw new UnsupportedOperationException("unknown raw key type, key type:" + key.getClass().getName());
		}
	}


	private static final Map<String, String> phoneTypeMap = new HashMap<>();

	static {
		phoneTypeMap.put(IOS_CLIENT, "ios");
		phoneTypeMap.put(ANDROID_CLIENT, "android");
		phoneTypeMap.put(WEB_CLIENT, "web");
		phoneTypeMap.put(H5_CLIENT, "h5");
		phoneTypeMap.put(MAC_CLIENT, "mac");
		phoneTypeMap.put(WINDOWS_CLIENT, "windows");
	}

	public static String getPhoneTypeValue(String phoneType) {
		return phoneTypeMap.get(phoneType);
	}
}
 
