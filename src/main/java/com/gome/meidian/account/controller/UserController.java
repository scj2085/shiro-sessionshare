//package com.gome.meidian.account.controller;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.octo.captcha.Captcha;
//import com.octo.captcha.service.captchastore.CaptchaStore;
//import com.octo.captcha.service.image.AbstractManageableImageCaptchaService;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.session.Session;
//import org.hibernate.validator.constraints.NotBlank;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.messaging.Source;
//import org.springframework.context.ApplicationContext;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.constraints.Pattern;
//import javax.validation.constraints.Size;
//import java.io.IOException;
//import java.util.*;
//
///**
// * 激活用户
// *
// * @author skyler
// * @time 2016-05-23
// */
//@RestController
//@RequestMapping("/user")
//@Validated
//public class UserController {
//
//	private static Logger log = LoggerFactory.getLogger(UserController.class);
//
//	@Resource
//	private IStaffService staffService;
//	@Resource
//	private IDeptService deptService;
//	@Resource
//	private ICompanyService companyService;
//	@Resource
//	private IUserService userService;
//	@Resource
//	private AbstractManageableImageCaptchaService captchaService;
//	@Resource
//	private IStaffInviteService staffInviteService;
//	@Resource
//	private CaptchaStore captchaStore;
//	@Resource
//	private ImUserClient imUserClient;
//    @Autowired
//    private Source source;
//    @Resource
//	private IChannelService channelService;
//    @Resource
//	private IMMicroClient imMicroClient;
//	@Autowired
//	private ApplicationContext applicationContext;
//	@Autowired
//    private IAuthStaffService iAuthStaffService;
//	@Autowired
//	private ChannelStaffNumUtil channelStaffNumUtil;
//	
//	/**
//	 * @param mobile 手机号码
//	 * @param inviteCode 邀请码
//	 * @param authCode 手机验证码
//	 * @throws MeiproException
//	 * @description: 验证手机号与手机验证码有效性
//	 * @author: yaoliang
//	 * @time: 2016年6月28日 下午3:57:46
//	 */
//	@RequestMapping("/validate")
//	public ResponseJson validate(@PathVariable String version,
//			@NotBlank(message = "{work.user.validateMobile.isNull}") @Pattern(regexp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$", message = "{work.user.validateMobile.errorMobile}") @RequestParam String mobile,
//			@NotBlank(message = "{work.user.validate.inviteCode.isNull}") @RequestParam String inviteCode,
//			@NotBlank(message = "{work.user.validate.authCode.isNull}") @RequestParam String authCode,
//			HttpServletRequest req) throws MeiproException {
//
//		ResponseJson json = new ResponseJson();
//		// 数据库是否有此记录
//		Staff staff = staffService.verifyStaff(mobile);
//
//		// 检查邀请码
//		staffInviteService.verifyInviteCode(staff.getId(), mobile, inviteCode);
//
//		// 检查验证码
//		userService.verifyAuthCode(mobile, authCode);
//
//		Company company = companyService.findById(staff.getCompanyId());
//		Map<String, Object> result = new HashMap<String, Object>();
//		result.put("companyName", company.getCompanyName());
//		result.put("mobile", mobile);
//
//		// uuid用于验证和激活操作是否为同一个用户
//		String uuid = UUID.randomUUID().toString();
//		userService.redisSetValue(uuid, JSONUtils.toJSONString(staff), Constants.MSG_SESSION_EXPIRE_TIME);
//		result.put("validate_uuid", uuid);
//
//		json.setData(result);
//		return json;
//	}
//
//
//	/**
//	 * @description: 调用im服务接口:userCheck、register
//	 * @param userId
//	 * @param mobile
//	 * @author: skyler
//	 * @time: 2016年11月24日 下午5:47:31
//	 */
//	private void registerUserId2IM(long userId, String mobile) {
//		try {
//            Map<String, Object> sdto = new HashMap<>();
//            sdto.put("userId", userId);
//            sdto.put("phoneNumber", mobile);
//            sdto.put("platform", IMHelper.clientConvert(MeiproEnvironment.getClient()));
//            sdto.put("osIdentifier", MeiproEnvironment.getAppVersion());
//            sdto.put("hardwareIndentifier", MeiproEnvironment.getPhoneType());
//            sdto.put("deviceId", MeiproEnvironment.getDevId());
//
//			long imUserId = imUserClient.register(JSONUtils.toJSONString(sdto));
//
//			if (imUserId != -1) {
//                Map<String, Object> param = Maps.newHashMap();
//                param.put("id", userId);
//                param.put("imUserId", imUserId);
//                log.info("im：register-->retrun data:{}", imUserId);
//                // 将imUserId保存到user表
//                userService.updateImUserId(param);
//            }
//		} catch(Exception e) {
//			e.printStackTrace();
//			log.error("im invoke failure：" + e.getMessage());
//		}
//	}
//
//	@RequestMapping("/user_info")
//	public ResponseJson userInfo(@PathVariable String version, String username, String password) {
//		ResponseJson json = new ResponseJson();
//		MeiproSession session = new MeiproSession();
//
//		UserInfo userInfo = session.getUserInfo();
//		if (userInfo.getStaffs() != null
//                && !userInfo.getStaffs().isEmpty()) {
//            Iterator<StaffInfo> iterator = userInfo.getStaffs().iterator();
//            StaffInfo staffInfo;
//            while (iterator.hasNext()) {
//                staffInfo = iterator.next();
//                if (staffInfo.getStatus() == Constants.DEL_STATUS) {
//                    iterator.remove();
//                }
//            }
//        }
//		json.setData(session.getUserInfo());
//		return json;
//	}
//
//	/**
//	 * @description: 发送图形验证码
//	 * @author: yaoliang
//	 * @time: 2016年7月16日 下午2:40:17
//	 */
//	@RequestMapping(value = "/send_picture_captcha", method = RequestMethod.GET)
//	public ResponseJson sendPictureCaptcha(String time, HttpServletRequest request, HttpServletResponse response)
//			throws IOException {
//		Session session = new MeiproSession().getSession();
//		session.setTimeout(Constants.PICTURE_CAPTCHA_EXPIRE_TIME*1000);
//		String sessionId = session.getId().toString();
//
//		//修复重写captchaStore后，captcha序列化后赋值带来的问题
//		captchaStore.removeCaptcha(sessionId);
//		SenderUtils.sendPictureCaptcha(captchaService, sessionId, request, response);
//		return new ResponseJson();
//	}
//
//	/**
//	 * @description: 发送短信验证码
//	 * @param mobile 发送的短信验证码的手机号码
//	 * @param pictureCaptcha 图形验证码
//	 * @author: yaoliang
//	 * @throws IOException
//	 * @time: 2016年7月16日 下午2:40:17
//	 */
//	@RequestMapping(value = "/send_msg_captcha", method = RequestMethod.POST)
//	public ResponseJson sendMsgCaptcha(
//			@NotBlank(message = "{work.user.validateMobile.isNull}") @RequestParam String mobile, String pictureCaptcha)
//			throws MeiproException, IOException {
//
//		// 判断是否为客户端，如果为客户端，跳过图形验证码
//		if (!MeiproEnvironment.getClient().equals(Constants.ANDROID_CLIENT)
//				&& !MeiproEnvironment.getClient().equals(Constants.IOS_CLIENT)
//				&& !MeiproEnvironment.getClient().equals(Constants.H5_CLIENT)) {
//
//			String sessionId = new MeiproSession().getSession().getId().toString();
//			Captcha captcha = captchaStore.getCaptcha(sessionId);
//			if (captcha == null) throw new ServiceException("work.user.pictureCaptcha.timeout");
//			// 校验用户输入端图形验证码是否正确
//			boolean isResponseCorrect = captcha.validateResponse(pictureCaptcha.toLowerCase());
//			captchaStore.removeCaptcha(sessionId);
//
//			if (!isResponseCorrect) {
//				throw new ServiceException("work.user.pictureCaptcha.wrong");
//			}
//		}
//
//		String key = Constants.REDIS_SEND_MSG_KEY_PREFIX + mobile;
//		// 检查验证码发送频率
//		userService.validateTimeDelay(key);
//		// 产生验证码
//		String authCode = RandomStringUtils.randomNumeric(Constants.CODE_DIGIT);
//		// 保存验证码到redis
//		userService.redisSetValue(key, authCode + "-" + System.currentTimeMillis(), Constants.MSG_SESSION_EXPIRE_TIME);
//		// 发短信
//		userService.sendSMS(mobile, authCode);
//
//		return ResponseJson.getSuccessResponse();
//	}
//
//	/**
//	 * 修改密码
//	 * 
//	 * @param version
//	 * @param changePasswordVo
//	 * @return
//	 * @throws MeiproException
//	 */
//	@RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
//	public ResponseJson modifyPassword(@PathVariable String version,
//			@Validated @RequestBody ChangePasswordVo changePasswordVo) throws MeiproException {
//
//		MeiproSession session = new MeiproSession();
//		UserInfo userInfo = session.getUserInfo();
//		userService.modifyPassword(userInfo, changePasswordVo);
//		return ResponseJson.getSuccessResponse();
//	}
//
//	/**
//	 * @description: 发送图形验证码
//	 */
//	@RequestMapping(value = "/send_pic_captcha", method = RequestMethod.GET)
//	public ResponseJson sendPicCaptcha(@PathVariable String version,
//			@NotBlank(message = "{work.user.validate_uuid.isNull}") @Size(min = 1, message = "{work.user.validate_uuid.isNull}") @RequestParam String uuid,
//			HttpServletRequest request, HttpServletResponse response) throws MeiproException, IOException {
//
//		String basic_key = getDevId() + uuid;
//		String sessionId = Constants.REDIS_SEND_PIC_KEY_PREFIX + basic_key;
//		SenderUtils.sendPictureCaptcha(captchaService, sessionId, request, response);
//
//		return new ResponseJson();
//	}
//
//	/**
//	 * @description: 发送短信验证码
//	 */
//	@RequestMapping(value = "/send_sms_captcha", method = RequestMethod.POST)
//	public ResponseJson sendSmsCaptcha(@PathVariable String version,
//			@NotBlank(message = "{work.user.validateMobile.isNull}") @Pattern(regexp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$", message = "{work.user.validateMobile.errorMobile}") @RequestParam String mobile,
//			@NotBlank(message = "{work.user.validatePicCaptcha.isNull}") @Size(min = 1, message = "{work.user.validatePicCaptcha.isNull}") @RequestParam String picCaptcha,
//			@NotBlank(message = "{work.user.validate_uuid.isNull}") @Size(min = 1, message = "{work.user.validate_uuid.isNull}") @RequestParam String uuid)
//			throws MeiproException, IOException {
//		
//		String devId = getDevId();
//
//		String basic_key = devId + mobile;
//
//		// 检查一天发送短信验证码次数获取：超过10次提示
//		String count_key = Constants.REDIS_SEND_SMS_COUNT_KEY_PREFIX + mobile;
//		int count = userService.validateSendSMSCount(count_key);
//
//		String pic_key = Constants.REDIS_SEND_PIC_KEY_PREFIX + devId + uuid;
//		String sms_key = Constants.REDIS_SEND_SMS_KEY_PREFIX + basic_key;
//		// 校验手机号
//		User user = userService.findUserByMobile(mobile);
//		if (user == null) {
//			throw new ServiceException("work.user.noExist");
//		}
//		// 验证图形验证码
//		userService.verifyPicCaptcha(captchaStore, pic_key, picCaptcha);
//
//		// 发送短信验证码
//		userService.sendSMSCaptcha(sms_key, mobile, RandomStringUtils.randomNumeric(Constants.CODE_DIGIT), 1);
//
//		//记录一天发送短息次数
//		userService.redisSetValue(count_key, ++count + "", (int) (Constants.SESSION_EXPIRE_TIME / 1000));
//
//		//user信息存到redis中
//		userService.redisSetValue(basic_key, JSONUtils.toJSONString(user), Constants.MSG_SESSION_EXPIRE_TIME);
//
//		return ResponseJson.getSuccessResponse();
//	}
//
//	/**
//	 * @description: 验证短信验证码
//	 */
//	@RequestMapping(value = "/verify_sms_captcha", method = RequestMethod.POST)
//	public ResponseJson verifySmsCaptcha(@PathVariable String version,
//			@NotBlank(message = "{work.user.validateMobile.isNull}") @Pattern(regexp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$", message = "{work.user.validateMobile.errorMobile}") @RequestParam String mobile,
//			@NotBlank(message = "{work.user.validatePicCaptcha.isNull}") @Size(min = 1, message = "{work.user.validatePicCaptcha.isNull}") @RequestParam String picCaptcha,
//			@NotBlank(message = "{work.user.validate.authCode.isNull}") @Size(min = 1, message = "{work.user.validate.authCode.isNull}") @RequestParam String smsCaptcha,
//			@NotBlank(message = "{work.user.validate_uuid.isNull}") @Size(min = 1, message = "{work.user.validate_uuid.isNull}") @RequestParam String uuid)
//			throws MeiproException {
//		
//		String devId = getDevId();
//
//		String basic_key = devId + mobile;
//
//		String pic_key = Constants.REDIS_SEND_PIC_KEY_PREFIX + devId + uuid;
//		String sms_key = Constants.REDIS_SEND_SMS_KEY_PREFIX + basic_key;
//		// 验证图形验证码
//		userService.verifyPicCaptcha(captchaStore, pic_key, picCaptcha);
//		// 成功验证，此条图形验证码即失效
//		userService.redisDelValue(pic_key);
//
//		// 验证短信验证码
//		userService.verifySMSCaptcha(sms_key, smsCaptcha);
//
//		String jsonUser = userService.redisGetValue(basic_key);
//		if (StringUtils.isBlank(jsonUser)) {
//			return new ResponseJson(StatusCode.ILLEGAL_OPERATION);
//		}
//		userService.redisSetValue(devId + uuid, jsonUser, Constants.MSG_SESSION_EXPIRE_TIME);
//
//		return new ResponseJson();
//	}
//
//	/**
//	 * @description: 重置密码
//	 */
//	@RequestMapping(value = "/reset_pwd", method = RequestMethod.POST)
//	public ResponseJson resetPwd(@PathVariable String version, @Validated @RequestBody ResetPwdVo vo)
//			throws MeiproException {
//		String devId = getDevId();
//		return userService.resetPwd(vo, devId);
//	}
//
//	/**
//	 * 
//	 * @description: 获取客户端类型
//	 */
//	public String getDevId() throws MeiproException {
//		String devId = MeiproEnvironment.getDevId();
//		if (StringUtils.isBlank(devId)) {
//			throw new ServiceException("work.operate.irregular");
//		}
//		return devId + ":";
//	}
//
//
//	/**
//	 * 验证密码
//	 * @param password
//	 * @return
//	 * @throws MeiproException
//	 */
//	@RequestMapping(value = "/verifyPassword", method = RequestMethod.GET)
//	public ResponseJson verifyPassword(@NotBlank(message = "{work.user.changepwd.oldpwd.isNull}")
//									   @RequestParam(value = "password")String password)throws MeiproException{
//		MeiproSession session = new MeiproSession();
//		UserInfo userInfo = session.getUserInfo();
//		userService.verifyPassword(userInfo, password);
//		return ResponseJson.getSuccessResponse();
//	}
//
//	@RequestMapping(value = "/sendMsgCodeForModifyMobile", method = RequestMethod.GET)
//	public ResponseJson sendMsgCodeForModifyMobile(@NotBlank(message = "{work.user.validateMobile.isNull}") @RequestParam String mobile)throws MeiproException, IOException {
//		MeiproSession session = new MeiproSession();
//		UserInfo userInfo = session.getUserInfo();
//		if(userInfo.getMobile().equals(mobile)){
//			throw new ServiceException("work.user.NewAndOldPhone.isSame");
//		}
//		sendMsgCodePubPart(mobile);
//		return ResponseJson.getSuccessResponse();
//	}
//
//	private void sendMsgCodePubPart(String mobile)throws MeiproException ,IOException{
//
//		String devId = getDevId();
//
//		String basic_key = devId + mobile;
//
//		// 检查一天发送短信验证码次数获取：超过10次提示
//		String count_key = Constants.REDIS_SEND_SMS_COUNT_KEY_PREFIX + mobile;
//		int count = userService.validateSendSMSCount(count_key);
//
//		String sms_key = Constants.REDIS_SEND_SMS_KEY_PREFIX + basic_key;
//
//		// 发送短信验证码
//		userService.sendSMSCaptcha(sms_key, mobile, RandomStringUtils.randomNumeric(Constants.CODE_DIGIT), 3);
//
//		//记录一天发送短息次数
//		userService.redisSetValue(count_key, ++count + "", (int) (Constants.SESSION_EXPIRE_TIME / 1000));
//
//	}
//	@RequestMapping(value = "/modifyMobile", method = RequestMethod.POST)
//	public ResponseJson modifyMobile(@Validated @RequestBody ModifyMobileVo modifyMobileVo) throws MeiproException {
//
//		String devId = getDevId();
//		String basic_key = devId + modifyMobileVo.getMobile();
//		String sms_key = Constants.REDIS_SEND_SMS_KEY_PREFIX + basic_key;
//		// 验证短信验证码
//		userService.verifySMSCaptcha(sms_key, modifyMobileVo.getSmsCaptcha());
//		User user = userService.findUserByMobile(modifyMobileVo.getMobile());
//		if(user != null){//判断新手机号是否被注册过
//			throw new ServiceException("work.user.userMobile.alreadyExists");
//		}
//		MeiproSession session = new MeiproSession();
//		UserInfo userInfo = session.getUserInfo();
//		userService.modifyMobile(modifyMobileVo, userInfo);
//		if(modifyMobileVo.getCompanyIds() != null && modifyMobileVo.getCompanyIds().size() > 0){
//			applicationContext.publishEvent(new ModifyMobileEvent(staffService.getStaffInfoByUserId(userInfo.getId()),session.getSession().getId(),modifyMobileVo.getMobile()));
//		}else{
//			applicationContext.publishEvent(new ModifyMobileEvent(null,session.getSession().getId(),modifyMobileVo.getMobile()));
//		}
//		return ResponseJson.getSuccessResponse();
//	}
//	
//
//}
