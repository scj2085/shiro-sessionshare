package com.gome.meidian.account.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.session.Session;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gome.meidian.account.mobileverificationcode.ChangePasswordVo;
import com.gome.meidian.account.mobileverificationcode.IUserService;
import com.gome.meidian.account.mobileverificationcode.ResetPwdVo;
import com.gome.meidian.account.shiro.Constants;
import com.gome.meidian.account.shiro.EncryptUtils;
import com.gome.meidian.account.shiro.MeiproSession;
import com.gome.meidian.account.shiro.StatusCode;
import com.gome.meidian.account.shiroimagecode1.MeiproEnvironment;
import com.gome.meidian.account.shiroimagecode1.SenderUtils;
import com.gome.meidian.account.shiroimagecode2.DrawIdentifyImgUtil;
import com.gome.meidian.account.shiroimagecode3.ValidateCode;
import com.gome.meidian.common.exception.MeidianException;
import com.gome.meidian.common.exception.ServiceException;
import com.gome.meidian.companyapi.service.CompanyUpdateService;
import com.gome.meidian.companyapi.vo.UserVo;
import com.gome.meidian.restfulcommon.reponse.ResponseJson;
import com.gome.meidian.restfulcommon.utils.JSONUtils;
import com.gome.meidian.restfulcommon.utils.StringUtils;
import com.octo.captcha.Captcha;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.image.AbstractManageableImageCaptchaService;


@RestController
@RequestMapping("/register")
public class RegisterController {

	@Autowired
	private CompanyUpdateService companyUpdateService;
	@Autowired
	private CaptchaStore captchaStore;
	@Autowired
	private AbstractManageableImageCaptchaService captchaService;
	@Resource
	private IUserService userService;
	
	@RequestMapping(value = "/addUser", method=RequestMethod.GET)
	public ResponseJson addUser(@RequestParam("accountName") String accountName, @RequestParam("password") String password) throws ServiceException{
		String salt = EncryptUtils.randomSalt();
		String pwd = EncryptUtils.encode(password, salt);
		
		UserVo userVo = new UserVo();
		userVo.setAccountName(accountName);
		userVo.setPassword(pwd);
		userVo.setSalt(salt);
		companyUpdateService.add(userVo);
		
		ResponseJson responseJson = new ResponseJson();
		return responseJson;
	}
	
	/** 
	 * 生成图形验证码
	 * @return 
	 */  
	@RequestMapping(value="/validateCode")  
	public String validateCode(HttpServletRequest request,HttpServletResponse response) throws Exception{  
	    // 设置响应的类型格式为图片格式  
	    response.setContentType("image/jpeg");  
	    //禁止图像缓存。  
	    response.setHeader("Pragma", "no-cache");  
	    response.setHeader("Cache-Control", "no-cache");  
	    response.setDateHeader("Expires", 0);  
	  
//	    HttpSession session = request.getSession(); 
	    
	    ValidateCode vCode = new ValidateCode(120,40,5,100); 
	    
	    MeiproSession meiproSession = new MeiproSession();
	    meiproSession.getSession().setAttribute("code", vCode.getCode());
//	    session.setAttribute("code", vCode.getCode());  
	    System.err.println(vCode.getCode());
	    vCode.write(response.getOutputStream());  
	    return null; 
	}
	
	/**
	 * 校验图形验证码
	 * @param code
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/checkCode", method = RequestMethod.GET)
	public String checkCode(@RequestParam("code") String code,HttpServletRequest request){
//		HttpSession session = request.getSession(); 
		MeiproSession meiproSession = new MeiproSession();
		Session session = meiproSession.getSession();
		String sessionCode = (String) session.getAttribute("code");  
		if (!StringUtils.equalsIgnoreCase(code, sessionCode)) {  //忽略验证码大小写  
			throw new RuntimeException("验证码对应不上code=" + code + "  sessionCode=" + sessionCode);  
		} 
		return "ok";
	}

	/**
	 * 生成图形验证码
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/imageCode", method = RequestMethod.GET)
	public String imageCode(HttpServletRequest request,HttpServletResponse response) {
		
		DrawIdentifyImgUtil draw = new DrawIdentifyImgUtil();
		String codeStr = draw.genCodeStr(4);
		
		BufferedImage image = draw.drawImg(codeStr);

		OutputStream os = null;
		try {
			os = response.getOutputStream();
			ImageIO.write(image, "PNG", os);
			image.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	
	
	
	
	
//	--------------一下为公司-------------------
	
	
	
	
	/**
	 * 公司图形验证码(没有uuid)
	 * @param time
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/sendPictureCaptcha", method = RequestMethod.GET)
	public ResponseJson sendPictureCaptcha(String time, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Session session = new MeiproSession().getSession();
		session.setTimeout(Constants.PICTURE_CAPTCHA_EXPIRE_TIME*1000);
		String sessionId = session.getId().toString();

		//修复重写captchaStore后，captcha序列化后赋值带来的问题
		captchaStore.removeCaptcha(sessionId);
		SenderUtils.sendPictureCaptcha(captchaService, sessionId, request, response);
		return new ResponseJson();
	}
	
	/**
	 * @description: 发送图形验证码(参数有uuid)
	 * @param uuid 手机号
	 * @param request
	 * @param response
	 * @author: skyler
	 * @time: 2016年9月22日 下午2:40:35
	 */
	@RequestMapping(value = "/sendPicCaptchaUuid", method = RequestMethod.GET)
	public ResponseJson sendPicCaptchaUuid(
			@NotBlank(message = "{work.user.validate_uuid.isNull}") @Size(min = 1, message = "{work.user.validate_uuid.isNull}") @RequestParam String uuid,
			HttpServletRequest request, HttpServletResponse response) throws MeidianException, IOException {

		String basic_key = getDevId() + uuid;
		String sessionId = Constants.REDIS_SEND_PIC_KEY_PREFIX + basic_key;
		SenderUtils.sendPictureCaptcha(captchaService, sessionId, request, response);

		return new ResponseJson();
	}
	
	/**
	 * 发送短信验证码（没有uuid）
	 * @param mobile 发送的短信验证码的手机号码
	 * @param pictureCaptcha 图形验证码
	 * @return
	 * @throws MeiproException
	 * @throws IOException
	 */
	@RequestMapping(value = "/sendMsgCaptcha", method = RequestMethod.POST)
	public ResponseJson sendMsgCaptcha(
			@NotBlank(message = "{work.user.validateMobile.isNull}") @RequestParam String mobile, String pictureCaptcha)
			throws MeidianException, IOException {

		// 判断是否为客户端，如果为客户端，跳过图形验证码
		if (!MeiproEnvironment.getClient().equals(Constants.ANDROID_CLIENT)
				&& !MeiproEnvironment.getClient().equals(Constants.IOS_CLIENT)
				&& !MeiproEnvironment.getClient().equals(Constants.H5_CLIENT)) {

			String sessionId = new MeiproSession().getSession().getId().toString();
			Captcha captcha = captchaStore.getCaptcha(sessionId);
			if (captcha == null) throw new ServiceException("work.user.pictureCaptcha.timeout");
			// 校验用户输入端图形验证码是否正确
			boolean isResponseCorrect = captcha.validateResponse(pictureCaptcha.toLowerCase());
			//此处不应该删
//			captchaStore.removeCaptcha(sessionId);

			if (!isResponseCorrect) {
				throw new ServiceException("work.user.pictureCaptcha.wrong");
			}
		}

		String key = Constants.REDIS_SEND_MSG_KEY_PREFIX + mobile;
		// 检查验证码发送频率
		userService.validateTimeDelay(key);
		// 产生验证码
		String authCode = RandomStringUtils.randomNumeric(Constants.CODE_DIGIT);
		System.err.println("短信验证码：========" + authCode);
		// 保存验证码到redis
		userService.redisSetValue(key, authCode + "-" + System.currentTimeMillis(), Constants.MSG_SESSION_EXPIRE_TIME);
		//user信息存到redis中
		UserVo user = userService.getUser("张三");
		String devId = getDevId();
		String basic_key = devId + mobile;
		userService.redisSetValue(basic_key, JSONUtils.toJSONString(user), Constants.MSG_SESSION_EXPIRE_TIME);
		// 发短信
		userService.sendSMS(mobile, authCode);

		return ResponseJson.getSuccessResponse();
	}
	
	/**
	 * @description: 发送短信验证码（有uuid，type 发送类型 1-修改密码，2-创建公司）
	 * @param mobile 手机号
	 * @param picCaptcha 图形验证码
	 * @return
	 * @author: skyler
	 * @throws IOException
	 * @time: 2016年9月22日 下午2:42:00
	 */
	@RequestMapping(value = "/sendSmsCaptchaUuid", method = RequestMethod.POST)
	public ResponseJson sendSmsCaptchaUuid(
			@NotBlank(message = "{work.user.validateMobile.isNull}") @Pattern(regexp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$", message = "{work.user.validateMobile.errorMobile}") @RequestParam String mobile,
			@NotBlank(message = "{work.user.validatePicCaptcha.isNull}") @Size(min = 1, message = "{work.user.validatePicCaptcha.isNull}") @RequestParam String picCaptcha,
			@NotBlank(message = "{work.user.validate_uuid.isNull}") @Size(min = 1, message = "{work.user.validate_uuid.isNull}") @RequestParam String uuid)
			throws MeidianException, IOException {
		
		String devId = getDevId();
		String basic_key = devId + mobile;

		// 检查一天发送短信验证码次数获取：超过10次提示
		String count_key = Constants.REDIS_SEND_SMS_COUNT_KEY_PREFIX + mobile;
		int count = userService.validateSendSMSCount(count_key);

		String pic_key = Constants.REDIS_SEND_PIC_KEY_PREFIX + devId + uuid;
		String sms_key = Constants.REDIS_SEND_SMS_KEY_PREFIX + basic_key;
		// 校验手机号
//		User user = userService.findUserByMobile(mobile);
		//测试
		UserVo user = userService.getUser("张三");
		if (user == null) {
			throw new ServiceException("work.user.noExist");
		}
		// 验证图形验证码
		userService.verifyPicCaptcha(captchaStore, pic_key, picCaptcha);

		// 发送短信验证码
		userService.sendSMSCaptcha(sms_key, mobile, RandomStringUtils.randomNumeric(Constants.CODE_DIGIT), 1);

		//记录一天发送短息次数
		userService.redisSetValue(count_key, ++count + "", (int) (Constants.SESSION_EXPIRE_TIME / 1000));

		//user信息存到redis中
		userService.redisSetValue(basic_key, JSONUtils.toJSONString(user), Constants.MSG_SESSION_EXPIRE_TIME);

		return ResponseJson.getSuccessResponse();
	}
	
	/**
	 * @description: 验证短信验证码（有uuid）
	 * @param mobile 手机号
	 * @param smsCaptcha 短信验证码
	 * @return
	 */
	@RequestMapping(value = "/verifySmsCaptchaUuid", method = RequestMethod.POST)
	public ResponseJson verifySmsCaptchaUuid(
			@NotBlank(message = "{work.user.validateMobile.isNull}") @Pattern(regexp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$", message = "{work.user.validateMobile.errorMobile}") @RequestParam String mobile,
			@NotBlank(message = "{work.user.validatePicCaptcha.isNull}") @Size(min = 1, message = "{work.user.validatePicCaptcha.isNull}") @RequestParam String picCaptcha,
			@NotBlank(message = "{work.user.validate.authCode.isNull}") @Size(min = 1, message = "{work.user.validate.authCode.isNull}") @RequestParam String smsCaptcha,
			@NotBlank(message = "{work.user.validate_uuid.isNull}") @Size(min = 1, message = "{work.user.validate_uuid.isNull}") @RequestParam String uuid)
			throws MeidianException {
		
		String devId = getDevId();

		String basic_key = devId + mobile;

		String pic_key = Constants.REDIS_SEND_PIC_KEY_PREFIX + devId + uuid;
		String sms_key = Constants.REDIS_SEND_SMS_KEY_PREFIX + basic_key;
		// 验证图形验证码
		userService.verifyPicCaptcha(captchaStore, pic_key, picCaptcha);
		// 成功验证，此条图形验证码即失效
		userService.redisDelValue(pic_key);

		// 验证短信验证码
		userService.verifySMSCaptcha(sms_key, smsCaptcha);

		String jsonUser = userService.redisGetValue(basic_key);
		if (StringUtils.isBlank(jsonUser)) {
			return new ResponseJson(StatusCode.ILLEGAL_OPERATION);
		}
		userService.redisSetValue(devId + uuid, jsonUser, Constants.MSG_SESSION_EXPIRE_TIME);

		return new ResponseJson();
	}
	
	/**
	 * @description: 验证短信验证码（没有uuid）
	 * @param mobile 手机号
	 * @param smsCaptcha 短信验证码
	 * @return
	 */
	@RequestMapping(value = "/verifySmsCaptcha", method = RequestMethod.POST)
	public ResponseJson verifySmsCaptcha(
			@NotBlank(message = "{work.user.validateMobile.isNull}") @Pattern(regexp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$", message = "{work.user.validateMobile.errorMobile}") @RequestParam String mobile,
			@NotBlank(message = "{work.user.validatePicCaptcha.isNull}") @Size(min = 1, message = "{work.user.validatePicCaptcha.isNull}") @RequestParam String picCaptcha,
			@NotBlank(message = "{work.user.validate.authCode.isNull}") @Size(min = 1, message = "{work.user.validate.authCode.isNull}") @RequestParam String smsCaptcha)
					throws MeidianException {
		
		// 验证图形验证码
		String sessionId = new MeiproSession().getSession().getId().toString();
		Captcha captcha = captchaStore.getCaptcha(sessionId);
		if (captcha == null) throw new ServiceException("work.user.pictureCaptcha.timeout");
		// 成功验证，此条图形验证码即失效
		userService.redisDelValue(sessionId);
		
		// 验证短信验证码
		String key = Constants.REDIS_SEND_MSG_KEY_PREFIX + mobile;
		userService.verifySMSCaptcha(key, smsCaptcha);
		
		//user信息存到redis中
		UserVo user = userService.getUser("张三");
		String devId = getDevId();
		String basic_key = devId + mobile;
		String jsonUser = userService.redisGetValue(basic_key);
		if (StringUtils.isBlank(jsonUser)) {
			return new ResponseJson(StatusCode.ILLEGAL_OPERATION);
		}
		// user信息存到redis中
		userService.redisSetValue(basic_key, jsonUser, Constants.MSG_SESSION_EXPIRE_TIME);
		return new ResponseJson();
	}
	
	/**
	 * @description: 验证图形验证码（有uuid）
	 * @param mobile 手机号
	 * @return
	 */
	@RequestMapping(value = "/checkimageCaptchaUuid", method = RequestMethod.POST)
	public ResponseJson checkimageCaptchaUuid(
			@NotBlank(message = "{work.user.validateMobile.isNull}") @Pattern(regexp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$", message = "{work.user.validateMobile.errorMobile}") @RequestParam String mobile,
			@NotBlank(message = "{work.user.validatePicCaptcha.isNull}") @Size(min = 1, message = "{work.user.validatePicCaptcha.isNull}") @RequestParam String picCaptcha,
			@NotBlank(message = "{work.user.validate_uuid.isNull}") @Size(min = 1, message = "{work.user.validate_uuid.isNull}") @RequestParam String uuid)
					throws MeidianException {
		
		String devId = getDevId();
		
		String basic_key = devId + mobile;
		
		String pic_key = Constants.REDIS_SEND_PIC_KEY_PREFIX + devId + uuid;
		String sms_key = Constants.REDIS_SEND_SMS_KEY_PREFIX + basic_key;
		// 验证图形验证码
		userService.verifyPicCaptcha(captchaStore, pic_key, picCaptcha);
		
		
		return new ResponseJson();
	}
	
	/**
	 * @description: 验证图形验证码（没有uuid）
	 * @param mobile 手机号
	 * @return
	 */
	@RequestMapping(value = "/checkimageCaptcha", method = RequestMethod.POST)
	public ResponseJson checkimageCaptcha(
			@NotBlank(message = "{work.user.validateMobile.isNull}") @Pattern(regexp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$", message = "{work.user.validateMobile.errorMobile}") @RequestParam String mobile,
			@NotBlank(message = "{work.user.validatePicCaptcha.isNull}") @Size(min = 1, message = "{work.user.validatePicCaptcha.isNull}") @RequestParam String picCaptcha)
					throws MeidianException {
		
		String sessionId = new MeiproSession().getSession().getId().toString();
		Captcha captcha = captchaStore.getCaptcha(sessionId);
		if (captcha == null) throw new ServiceException("work.user.pictureCaptcha.timeout");
		
		return new ResponseJson();
	}
	
	/**
	 * 修改密码
	 * 
	 * @param version
	 * @param changePasswordVo
	 * @return
	 * @throws MeiproException
	 */
	@RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
	public ResponseJson modifyPassword(@PathVariable String version,
			@Validated @RequestBody ChangePasswordVo changePasswordVo) throws MeidianException {

		MeiproSession session = new MeiproSession();
		UserVo userInfo = session.getUserInfo();
		userService.modifyPassword(userInfo, changePasswordVo);
		return ResponseJson.getSuccessResponse();
	}






	/**
	 * @description: 重置密码
	 * @param vo
	 * @throws MeiproException
	 * @author: skyler
	 * @time: 2016年9月23日 下午6:28:49
	 */
	@RequestMapping(value = "/reset_pwd", method = RequestMethod.POST)
	public ResponseJson resetPwd(@PathVariable String version, @Validated @RequestBody ResetPwdVo vo)
			throws MeidianException {
		String devId = getDevId();
		return userService.resetPwd(vo, devId);
	}

	/**
	 * 
	 * @description: 获取客户端类型
	 * @throws MeiproException
	 * @author: skyler
	 * @time: 2016年9月27日 下午5:35:05
	 */
	public String getDevId() throws MeidianException {
		String devId = MeiproEnvironment.getDevId();
		if (StringUtils.isBlank(devId)) {
			throw new ServiceException("work.operate.irregular");
		}
		return devId + ":";
	}

	/**
	 * 验证密码
	 * @param password
	 * @return
	 * @throws MeiproException
	 */
	@RequestMapping(value = "/verifyPassword", method = RequestMethod.GET)
	public ResponseJson verifyPassword(@NotBlank(message = "{work.user.changepwd.oldpwd.isNull}")
									   @RequestParam(value = "password")String password)throws MeidianException{
		MeiproSession session = new MeiproSession();
		UserVo userInfo = session.getUserInfo();
		userService.verifyPassword(userInfo, password);
		return ResponseJson.getSuccessResponse();
	}

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

	private void sendMsgCodePubPart(String mobile)throws MeidianException ,IOException{

		String devId = getDevId();

		String basic_key = devId + mobile;

		// 检查一天发送短信验证码次数获取：超过10次提示
		String count_key = Constants.REDIS_SEND_SMS_COUNT_KEY_PREFIX + mobile;
		int count = userService.validateSendSMSCount(count_key);

		String sms_key = Constants.REDIS_SEND_SMS_KEY_PREFIX + basic_key;

		// 发送短信验证码
		userService.sendSMSCaptcha(sms_key, mobile, RandomStringUtils.randomNumeric(Constants.CODE_DIGIT), 3);

		//记录一天发送短息次数
		userService.redisSetValue(count_key, ++count + "", (int) (Constants.SESSION_EXPIRE_TIME / 1000));

	}
}
