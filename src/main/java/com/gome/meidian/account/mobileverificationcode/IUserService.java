package com.gome.meidian.account.mobileverificationcode;

import com.gome.meidian.account.utils.User;
import com.gome.meidian.common.exception.MeidianException;
import com.gome.meidian.common.exception.ServiceException;
import com.gome.meidian.companyapi.vo.UserVo;
import com.gome.meidian.restfulcommon.reponse.ResponseJson;
import com.octo.captcha.service.captchastore.CaptchaStore;

import java.io.IOException;

public interface IUserService {

	int insert(User user);

	User findById(Long id);





	/**
	 * @description: 检查手机验证码
	 * @param mobile
	 * @param authCode
	 * @author: skyler
	 * @time: 2016年7月16日 下午3:54:28
	 */
	void verifyAuthCode(String mobile, String authCode) throws MeidianException;

	/**
	 * @description: 发送短信
	 * @param mobile
	 * @param smsCaptcha
	 * @author: skyler
	 * @throws IOException
	 * @time: 2016年7月18日 下午4:30:12
	 */
	void sendSMS(String msgTemplate, String mobile, String smsCaptcha) throws MeidianException, IOException;

	/**
	 * @description: 发送短信
	 * @param mobile
	 * @param smsCaptcha
	 * @author: skyler
	 * @throws IOException
	 * @time: 2016年7月18日 下午4:30:12
	 */
	void sendSMS(String mobile, String smsCaptcha) throws MeidianException, IOException;

	/**
	 * @description: redis存数据
	 * @author: skyler
	 * @time: 2016年7月18日 上午9:51:54
	 */
	void redisSetValue(String uuid, String mobile, int msgSessionExpireTime);

	/**
	 * @description: redis取数据
	 * @return value 数据的值
	 * @author: skyler
	 * @time: 2016年7月18日 上午9:51:54
	 */
	String redisGetValue(String uuid);


	/**
	 * @description: redis删除数据
	 * @author: skyler
	 * @time: 2016年7月18日 上午10:00:38
	 */
	void redisDelValue(String uuid);

	/**
	 * @description: 检查短信发送间隔：60秒内不允许发送
	 * @param key
	 * @author: skyler
	 * @time: 2016年7月18日 下午1:43:40
	 */
	void validateTimeDelay(String key) throws MeidianException;

	/**
	 * 修改密码
	 *
	 * @param userInfo
	 * @param changePasswordVo
	 * @throws ServiceException
	 */
	public void modifyPassword(UserVo user, ChangePasswordVo changePasswordVo) throws MeidianException;

	/**
	 * 
	 * @description: 验证图形验证码
	 * @param captchaStore
	 * @param key
	 * @param picCaptcha
	 * @throws MeiproException
	 * @author: skyler
	 * @time: 2016年9月25日 下午5:30:15
	 */
	void verifyPicCaptcha(CaptchaStore captchaStore, String key, String picCaptcha) throws MeidianException;

	/**
	 * @description: 发送短信
	 * @param prefix
	 * @param mobile
	 * @param smsCaptcha
	 * @param type 发送类型 1-修改密码，2-创建公司
	 * @throws MeiproException
	 * @author: skyler
	 * @throws IOException
	 * @time: 2016年9月25日 下午5:29:31
	 */
	void sendSMSCaptcha(String prefix, String mobile, String smsCaptcha, int type)
			throws MeidianException, IOException;

	/**
	 * @description: 验证短信
	 * @param key
	 * @throws MeiproException
	 * @author: skyler
	 * @param devId
	 * @time: 2016年9月25日 下午5:29:31
	 */
	void verifySMSCaptcha(String key, String devId) throws MeidianException;

	/**
	 * 
	 * @description: 重置密码
	 * @param vo
	 * @return
	 * @throws MeiproException
	 * @author: skyler
	 * @time: 2016年9月25日 下午5:33:02
	 */
	ResponseJson resetPwd(ResetPwdVo vo, String devId) throws MeidianException;

	/**
	 * @description: 管理端重置密码
	 * @param user 用户
 	 * @param ramdomPwd 密码
	 * @throws MeiproException
	 * @throws IOException
	 * @author: skyler
	 * @time: 2016年9月28日 下午5:28:26
	 */
	void adminResetPwd(UserVo user, String ramdomPwd) throws MeidianException, IOException;

	/**
	 * @description: 访问次数限制
	 * @param key 
	 * @throws MeiproException
	 * @throws IOException
	 * @author: skyler
	 * @time: 2016年9月28日 下午5:28:26
	 */
	int validateSendSMSCount(String key) throws MeidianException;

	/**
	 * 验证密码
	 * @param userInfo
	 * @param password
	 * @throws ServiceException
	 */
	public void verifyPassword(UserVo user, String password) throws MeidianException;

	public UserVo getUser(String accountName) throws ServiceException;

}
