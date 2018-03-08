package com.gome.meidian.account.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gome.meidian.account.shiro.Constants;
import com.gome.meidian.account.shiro.MeiproSession;
import com.gome.meidian.account.shiro.StatusCode;
import com.gome.meidian.common.exception.ServiceException;
import com.gome.meidian.companyapi.vo.UserVo;
import com.gome.meidian.restfulcommon.reponse.ResponseJson;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/login/v1")
public class LoginController {
	
	@Autowired
	private ApplicationContext applicationContext;
   	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseJson login(@RequestParam String username, @RequestParam String password,
			@RequestParam boolean remember) {
		if (username == null || password == null) {
			logger.error("/login error username or password  null");
			return ResponseJson.getFailedResponse();

		}
		// ensureUserIsLoggedOut();
		ResponseJson responseJson = new ResponseJson();
		MeiproSession session = new MeiproSession();
		try {
			session.login(username, password, null, remember);
			session.getSession().setTimeout(Constants.SESSION_EXPIRE_TIME);
		} catch (AuthenticationException e) {
			if (e instanceof UnknownAccountException) {
				// 账户不存在
				logger.error("login error", e);
				return new ResponseJson(StatusCode.LOGIN_ACCOUNT_NOT_EXIST);
			} else if (e instanceof IncorrectCredentialsException) {
				// 密码不正确
				logger.error("login error", e);
				return new ResponseJson(StatusCode.LOGIN_ERROR);
			}
			return ResponseJson.getFailedResponse();
		}
		logger.warn("userId:{} login", session.getUserInfo().getId());

//		UserSecureEvent userSecureEvent = new UserSecureEvent(session.getUserInfo(), session.getSession(), UserSecureEvent.EventType.LOGIN);
//
//		applicationContext.publishEvent(userSecureEvent);
		return responseJson;
	}

	@RequestMapping(value = "/access_token", method = RequestMethod.POST)
	public ResponseJson accessToken(String username, String password)
            throws ServiceException {
		if (username == null || password == null) {
			logger.error("/access_token error username or password  null");
			return ResponseJson.getFailedResponse();
		}
		MeiproSession session = new MeiproSession();
		// app 默认失效时间：一个月，暂不提供refresh token
		ResponseJson responseJson = new ResponseJson();
		try {
			session.login(username, password, null, false);
			session.getSession().setTimeout(Constants.APP_SESSION_EXPIRE_TIME);

//			Token token = imUserClient.getToken(session.getUserInfo().getImUserId(), IMHelper.clientConvert(MeiproEnvironment.getClient()));

			Map<String, Object> map = new HashMap<>();
			map.put("token", session.getSession().getId());
			map.put("tokenExpirationTime", session.getSession().getTimeout());
//			map.put("imToken", token.getImToken());
//			map.put("imTokenExpirationTime", token.getImTokenExpirationTime());
			map.put("userInfo", session.getUserInfo());
			responseJson.setData(map);
		} catch (Exception e) {
			logger.error("access_token invoke error:{}", e.getMessage());
			if (e instanceof UnknownAccountException) {
				// 账户不存在
				logger.error("login error", e);
				return new ResponseJson(StatusCode.LOGIN_ACCOUNT_NOT_EXIST);
			} else if (e instanceof IncorrectCredentialsException) {
				// 密码不正确
				logger.error("login error", e);
				return new ResponseJson(StatusCode.LOGIN_ERROR);
			}
			return ResponseJson.getFailedResponse();
		}
		logger.warn("userId:{} login", session.getUserInfo().getId());
//		UserSecureEvent userSecureEvent = new UserSecureEvent(session.getUserInfo(), session.getSession(), UserSecureEvent.EventType.LOGIN);
//
//		applicationContext.publishEvent(userSecureEvent);
		return responseJson;
	}

	// TODO
	@RequestMapping("/refresh_token")
	public ResponseJson refreshToken(String username, String password) {
		return null;
	}

	@RequestMapping("/logout")
	public ResponseJson loginout() {
		MeiproSession session = new MeiproSession();

//		UserSecureEvent userSecureEvent = new UserSecureEvent(session.getUserInfo(), session.getSession(), UserSecureEvent.EventType.LOGOUT);
//
//		applicationContext.publishEvent(userSecureEvent);

		session.logout();

		return ResponseJson.getSuccessResponse();
	}

	@RequestMapping("/app_logout")
	public ResponseJson appLogout() {
		MeiproSession session = new MeiproSession();

//		UserSecureEvent userSecureEvent = new UserSecureEvent(session.getUserInfo(), session.getSession(), UserSecureEvent.EventType.LOGOUT);
//
//		applicationContext.publishEvent(userSecureEvent);

		try {
			session.logout();
		} catch (Exception e) {
			logger.error("app logout error", e);
		}

		return ResponseJson.getSuccessResponse();
	}

	private void ensureUserIsLoggedOut() {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			if (currentUser == null)
				return;
			currentUser.logout();
			Session session = currentUser.getSession(false);
			if (session == null)
				return;
			session.stop();
		} catch (Exception e) {
			logger.error("logout error", e);
		}
	}

	/**
     * 登录后获取权限
     * @return
     */
//    @RequestMapping(value = "/access_authorize", method = RequestMethod.GET)
//	public ResponseJson access_authorize(
//			@NotNull(message = "{uc.companyId.null}") @RequestParam("companyId") Long companyId) {
//    	ResponseJson responseJson = new ResponseJson();
//    	//自定返回类
//		PermissionInfo permissionInfo = new PermissionInfo();
//		//从session中获取员工id
//		MeiproSession meiproSession = new MeiproSession();
//		StaffInfo staffInfo = meiproSession.getUserInfo().getStaffInfoByCompanyId(companyId);
//		Long staffId = staffInfo.getId();
//
//		Subject subject = meiproSession.getSubject();
//		try {
//			//获取员工对应公司下的模块
//			List<String> modules = Lists.newArrayList();
//			List<Module> moduleList = moduleService.findModuleByStaffId(staffId);
//			for (Module module : moduleList) {
//				if (!module.getModuleCode().equals(Constants.DEFAULT_MODULE_CODE)) {
//					modules.add(module.getModuleCode());
//				}
//			}
//			//获取员工对应公司下的角色
//			List<Map<String, Object>> roles = Lists.newArrayList();
//			List<Role> roleList = roleService.findRoleByStaffId(staffId);
//			for (Role role : roleList) {
//				Map<String, Object> roleMap = Maps.newHashMap();
//				roleMap.put("roleName", role.getRoleName());
//				roleMap.put("roleCode", role.getRoleCode());
//				roles.add(roleMap);
//			}
//			//获取员工对应公司下前台的权限
//			List<Map<String, Object>> Permissions = Lists.newArrayList();
//			List<Permission> permList = permissionService.findPermByStaffId(staffId, Constants.WORK_PERMISSION_FLAG);
//			for (Permission permission : permList) {
//				Map<String, Object> permMap = Maps.newHashMap();
//				permMap.put("expression", permission.getExpression());
//				Permissions.add(permMap);
//			}
//
//			//if current staff has virtual admin role, add "custom-admin" to the role
//			boolean isAdmin = subject.hasRole(Constants.VIRTUAL_ADMIN_ROLE_CODE);
//
//			if(isAdmin){
//				Map<String, Object> roleMap = Maps.newHashMap();
//				roleMap.put("roleName", "新建管理员");
//				roleMap.put("roleCode", "custom-admin");
//				roles.add(roleMap);
//			}
//
//			permissionInfo.setModules(modules);
//			permissionInfo.setRoles(roles);
//			permissionInfo.setPermissions(Permissions);
//			responseJson.setData(permissionInfo);
//		} catch (AuthenticationException e) {
//			return ResponseJson.getFailedResponse();
//		}
//		return responseJson;
//	}
	
	@RequestMapping(value = "/test", method=RequestMethod.GET)
	public ResponseJson test(){
		ResponseJson responseJson = new ResponseJson();
		Subject subject = SecurityUtils.getSubject();
		UserVo userVo = (UserVo) subject.getPrincipal();
		responseJson.setData(userVo);
		return responseJson;
	}
}
