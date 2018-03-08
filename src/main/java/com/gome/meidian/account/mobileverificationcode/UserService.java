package com.gome.meidian.account.mobileverificationcode;

import com.gome.meidian.account.shiro.Constants;
import com.gome.meidian.account.shiro.EncryptUtils;
import com.gome.meidian.account.shiro.StatusCode;
import com.gome.meidian.account.shiroimagecode1.JedisUtils;
import com.gome.meidian.account.shiroimagecode1.SenderUtils;
import com.gome.meidian.account.utils.User;
import com.gome.meidian.common.exception.MeidianException;
import com.gome.meidian.common.exception.ServiceException;
import com.gome.meidian.companyapi.service.CompanyFindService;
import com.gome.meidian.companyapi.vo.UserVo;
import com.gome.meidian.restfulcommon.reponse.ResponseJson;
import com.gome.meidian.restfulcommon.utils.JSONUtils;
import com.google.common.collect.Lists;
import com.octo.captcha.image.gimpy.Gimpy;
import com.octo.captcha.service.captchastore.CaptchaStore;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

@Service
public class UserService implements IUserService {
	private static Logger log = LoggerFactory.getLogger(UserService.class);

	@Resource
	private JedisCluster jedisCluster;

	@Value("${message.captcha.send.template:}")
	private String msgTemplate;

	@Value("${message.captcha.send.template_reset:}")
	private String msgTemplate_reset;

	@Value("${message.captcha.send.template_register_company:}")
	private String msgRegisterCompany;

	@Value("${message.captcha.send.template_admin_reset:}")
	private String msgTemplate_admin_reset;

	@Value("${message.captcha.send.url:}")
	private String msgUrl;

	@Value("${message.send.sign:}")
	private String msgSign;

	@Value("${message.send.switch.close:true}")
	private String msg_switch_close;

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private CompanyFindService companyFindService;

	@Override
	public int insert(User user) {
//		return userDao.insert(user);
		return 0;
	}

	@Override
	public User findById(Long id) {
//		return userDao.findById(id);
		return null;
	}

//	/**
//	 * 保存user表并更新staff表userId值
//	 *
//	 * @author yaoliang
//	 * @time 2016-05-26
//	 */
//	@Override
//	@Transactional(rollbackFor = Throwable.class)
//	public long insertAndUpdate(Staff staff, String mobile, String password) throws MeiproException{
//
//		User oldUser = this.findUserByMobile(mobile);
//		if (oldUser != null) {
//			throw new ServiceException("work.user.active.nobind");
//		}
//
//		String salt = EncryptUtils.randomSalt();
//		String enrypassword = EncryptUtils.encode(password, salt);
//
//		User user = new User();
//		user.setMobile(mobile);
//		user.setPassword(enrypassword);
//		user.setSalt(salt);
//		user.setStatus(0);
//		user.setEmail(staff.getEmail());
//		user.setName(staff.getStaffName());
//		user.setNickName(staff.getStaffName());
//		// 保存数据
//		this.insert(user);
//
//		staff.setUserId(user.getId());
//
//		// 更新员工表staff的userId值
//		staffDao.update(staff);
//		staffInviteDao.updateStatus(StaffInvite.STATUS_USED, staff.getId());
//		log.info("更新staff成功", staff);
//
//		return user.getId();
//	}

//	/**
//	 * 更新user表imUserId值
//	 *
//	 * @author yaoliang
//	 * @time 2016-05-26
//	 */
//	@Override
//	@Transactional(rollbackFor = Throwable.class)
//	public int updateImUserId(long userId, long imUserId) {
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("id", userId);
//		param.put("imUserId", imUserId);
//
//		return userDao.updateImUserId(param);
//	}
//
//	@Override
//	public UserInfo findByMobile(String mobile) throws MeiproException {
//		User user = userDao.findByMobile(mobile);
//		if (user == null)
//			return null;
//		Long userId = user.getId();
//		List<Staff> staffList = staffDao.getStaffs(userId);
//		List<StaffInfo> staffs = Lists.newArrayList();
//		staffList.forEach(staff -> {
//			StaffInfo staffInfo = new StaffInfo();
//			staffInfo.setId(staff.getId());
//			staffInfo.setCompanyId(staff.getCompanyId());
//			staffInfo.setCompanyName(companyDao.findById(staff.getCompanyId()).getCompanyName());
//			staffInfo.setEmail(staff.getEmail());
//			staffInfo.setMobile(staff.getMobile());
//			staffInfo.setStaffName(staff.getStaffName());
//			staffInfo.setStaffNo(staff.getStaffNo());
//			staffs.add(staffInfo);
//		});
//
//		UserInfo userInfo = new UserInfo();
//		BeanUtils.copyProperties(user, userInfo);
//		userInfo.setStaffs(staffs);
//		return userInfo;
//	}

//	@Override
//	public User findUserByMobile(String mobile) {
//		return userDao.findByMobile(mobile);
//	}

	/**
	 * 检查手机验证码
	 */
	@Override
	public void verifyAuthCode(String mobile, String authCode) throws MeidianException {
		String key = Constants.REDIS_SEND_MSG_KEY_PREFIX + mobile;
		String value = redisGetValue(key);
		if (StringUtils.isBlank(value)) {
			throw new ServiceException("work.user.authCode.timeout");
		}

		String[] vals = value.split("-");
		if (!authCode.equalsIgnoreCase(vals[0])) {
			throw new ServiceException("work.user.authCode.wrong");
		}
	}
	
	/**
	 * 发送短信
	 */
	@Override
	public void sendSMS(String smsTemplate, String mobile, String smsCaptcha)
			throws MeidianException, IOException {
		String content = URLEncoder.encode(smsTemplate.replace("{0}", smsCaptcha), "UTF-8");
		log.info("time:" + LocalDateTime.now() + " mobile:" + mobile + " authCode:" + smsCaptcha + " template:" + smsTemplate + " sign:" + msgSign + " 开关是否关闭：" + msg_switch_close);
		String url = msgUrl.replace("{1}", URLEncoder.encode(msgSign, "UTF-8")).replace("{2}", mobile).replace("{3}",
				content);
		log.debug("msg send url:" + url);
		if (!Boolean.valueOf(msg_switch_close)) {
			SenderUtils.send(url);
		}
	}

	/**
	 * 发送短信
	 */
	@Override
	public void sendSMS(String mobile, String smsCaptcha) throws MeidianException, IOException {
		log.info("time:" + LocalDateTime.now() + " mobile:" + mobile + " authCode:" + smsCaptcha + " template:" + msgTemplate + " sign:" + msgSign  + " 开关是否关闭：" + msg_switch_close);
		String content = URLEncoder.encode(msgTemplate.replace("{0}", smsCaptcha), "UTF-8");
		String url = msgUrl.replace("{1}", URLEncoder.encode(msgSign, "UTF-8")).replace("{2}", mobile).replace("{3}",
				content);
		log.debug("msg send url:" + url);
		if (!Boolean.valueOf(msg_switch_close)) {
			SenderUtils.send(url);
		}
	}

	/**
	 * redis存数据
	 * 
	 */
	public void redisSetValue(String key, String value, int expire) {
		JedisUtils.process(jedisCluster, jedis -> {
			jedis.set(key, value);
			jedis.expire(key, expire);
			return null;
		});
	}

	/**
	 * redis取数据
	 */
	public String redisGetValue(String key) {
		String value = JedisUtils.process(jedisCluster, jedis -> {
			return jedis.get(key);
		});

		return value;
	}

	/**
	 * redis删除数据
	 */
	public void redisDelValue(String key) {
		JedisUtils.process(jedisCluster, jedis -> {
			jedis.del(key);
			return null;
		});
	}

	/**
	 * 检查短信发送间隔：60秒内不允许发送
	 */
	@Override
	public void validateTimeDelay(String key) throws MeidianException {
		String value = redisGetValue(key);
		// 判断发送频率是否超过60s
		if (value != null && !value.equals("")) {
			String[] vals = value.split("-");
			if (NumberUtils.subtract(System.currentTimeMillis() + "", vals[1]) < 60 * 1000) {
				throw new ServiceException("work.user.msgCaptcha.timeDelay");
			}
		}

	}

	/**
	 * 修改密码
	 * @param userInfo
	 * @param changePasswordVo
	 * @throws ServiceException
	 */
	@Override
//	@Transactional(rollbackFor = Throwable.class)
	public void modifyPassword(UserVo userInfo, ChangePasswordVo changePasswordVo) throws ServiceException {

//		User user = userDao.findById(userInfo.getId());
		UserVo user = getUser(userInfo.getAccountName());
		if (null == user) {
			log.error("经查无此用户!");
			throw new ServiceException("经查无此用户");
		}

		/* 1.原密码校验 */
		String salt = user.getSalt();
		String enrypassword = EncryptUtils.encode(changePasswordVo.getOldPwd(), salt);
		if (!user.getPassword().equals(enrypassword)) {
			throw new ServiceException("work.user.changepwd.oldpwd.error");
		}

		/* 2.新密码与确认密码比较 */
		if (!changePasswordVo.getNewPwd().equals(changePasswordVo.getVerifypwd())) {
			throw new ServiceException("work.user.changepwd.validatePassword.isnotSame");
		}

		/* 3.原密码与新密码重复校验 */
		if (changePasswordVo.getOldPwd().equals(changePasswordVo.getNewPwd())) {
			throw new ServiceException("work.user.changepwd.validatePassword.isSame");
		}

		String newSalt = EncryptUtils.randomSalt();
		String newPass = EncryptUtils.encode(changePasswordVo.getNewPwd(), newSalt);
		user.setPassword(newPass);
		user.setSalt(newSalt);

//		userDao.update(user);
		
		System.err.println("修改密码");
		applicationContext.publishEvent(user);

	}

	/**
	 * 验证图形验证码
	 */
	@Override
	public void verifyPicCaptcha(CaptchaStore captchaStore, String key, String picCaptcha) throws MeidianException {
		// 校验图形码
		Gimpy cap = (Gimpy) captchaStore.getCaptcha(key);
		if (cap == null) {
			throw new ServiceException("work.user.pictureCaptcha.timeout");
		}
		// 校验用户输入端图形验证码是否正确
		boolean isResponseCorrect = cap.validateResponse(picCaptcha.toLowerCase());

		if (!isResponseCorrect) {
			redisDelValue(key);
			throw new ServiceException("work.user.pictureCaptcha.wrong");
		}

	}

	@Override
	public void sendSMSCaptcha(String key, String mobile, String smsCaptcha, int type)
			throws MeidianException, IOException {

		// 检查验证码发送频率
		validateTimeDelay(key);

		//修改密码
		if(type == 1)
			sendSMS(msgTemplate_reset, mobile, smsCaptcha);
		//创建公司
		else if(type == 2)
			sendSMS(msgRegisterCompany, mobile, smsCaptcha);
		//通用发送短信验证码
		else if(type == 3)
			sendSMS(msgTemplate, mobile, smsCaptcha);
		// 保存验证码到redis
		redisSetValue(key, smsCaptcha + "-" + System.currentTimeMillis(),Constants.MSG_SESSION_EXPIRE_TIME);
	}

	/**
	 * 验证短信
	 */
	@Override
	public void verifySMSCaptcha(String key, String smsCaptcha) throws MeidianException {
		String value = redisGetValue(key);
		if (StringUtils.isBlank(value)) {
			throw new ServiceException("work.user.authCode.timeout");
		}

		String[] vals = value.split("-");
		if (!smsCaptcha.equalsIgnoreCase(vals[0])) {
			throw new ServiceException("work.user.authCode.wrong");
		}
		System.err.println("验证成功：短信码=====" + vals[0]);
		// 成功验证，此条短信即失效
		redisDelValue(key);
	}

	/**
	 * 重置密码
	 */
//	@Transactional(rollbackFor = Throwable.class)
	@Override
	public ResponseJson resetPwd(ResetPwdVo vo, String devId) throws MeidianException {
		String key = devId + vo.getUuid();
		String jsonUser = redisGetValue(key);
		if (StringUtils.isBlank(jsonUser)) {
			return new ResponseJson(StatusCode.ILLEGAL_OPERATION);
		}

		// 验证密码相等性
		if (!vo.getPassword().equals(vo.getVerifyPassword())) {
			throw new ServiceException("work.user.validateMobile.notConsistent");
		}

		User user = JSONUtils.convertValue(jsonUser, User.class);
		String salt = EncryptUtils.randomSalt();
		user.setSalt(salt);
		user.setPassword(EncryptUtils.encode(vo.getPassword(), salt));

		// userDao.update(user);
		System.err.println("修改密码");
		//修改密码后删除
		redisDelValue(key);

		return ResponseJson.getSuccessResponse();
	}

//	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void adminResetPwd(UserVo user, String ramdomPwd) throws MeidianException, IOException {
		log.info("admin reset user ramdomPwd:"+ramdomPwd);
		String pwd = Base64.encodeBase64String(ramdomPwd.getBytes());
		String salt = EncryptUtils.randomSalt();
		String enryPwd = EncryptUtils.encode(pwd, salt);
		user.setSalt(salt);
		user.setPassword(enryPwd);
//		userDao.update(user);
		System.err.println("管理端修改密码");
		//发送短信
//		sendSMS(msgTemplate_admin_reset, user.getMobile(), ramdomPwd);
	}
	
	/**
	 * 访问次数限制
	 */
	@Override
	public int validateSendSMSCount(String key) throws MeidianException {
		String value = redisGetValue(key);
		int count = 0;
		if (StringUtils.isNotBlank(value)) {
			count = Integer.parseInt(value);
			if (count == 10) {
				throw new ServiceException("work.user.SendSMSCount.limit");
			}
		}

		return count;
	}

	/**
	 * 验证密码
	 */
	@Override
	public void verifyPassword(UserVo userInfo, String password) throws ServiceException {
//		User user = userDao.findById(userInfo.getId());
		
		UserVo user = getUser(userInfo.getAccountName());
		if (null == user) {
			log.error("user not exist");
			throw new ServiceException("work.user.userNotExist");
		}

		/* 1.原密码校验 */
		String salt = user.getSalt();
		String enrypassword = EncryptUtils.encode(password, salt);
		if (!user.getPassword().equals(enrypassword)) {
			throw new ServiceException("work.user.password.error");
		}
	}

	public UserVo getUser(String accountName) throws ServiceException{
//		return companyFindService.find(accountName);
		//测试
		UserVo userVo = new UserVo();
		userVo.setAccountName("张三");
		userVo.setId(12L);
		userVo.setPassword("3f8890ebfa6bb6574f1fa4620cc517a78b8ac16a5f777895c1fb2f7f10e88199");
		userVo.setSalt("JQKmLSmF6BiAhescr0ciHQ==");
		return userVo;
	}
}
