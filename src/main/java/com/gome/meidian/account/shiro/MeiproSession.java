package com.gome.meidian.account.shiro;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gome.meidian.common.exception.ServiceException;
import com.gome.meidian.companyapi.vo.UserVo;


/**
 * 自定义session
 *
 */
public class MeiproSession implements Map<String, Object>, Serializable {

    private static final long serialVersionUID = -4132559960205379466L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MeiproSession.class);
    private Session session;
    private Subject subject;

    public UserVo getUserInfo() {
        return (UserVo) subject.getPrincipal();
    }

    public MeiproSession() {
        subject = SecurityUtils.getSubject();
        session = subject.getSession();
    }

    public Subject getSubject() {
        return subject;
    }

    public Session getSession() {
        return session;
    }

    public void login(String username, String password, String host, boolean remberMe) {
        UsernamePasswordToken upToken = new UsernamePasswordToken();
        upToken.setUsername(username);
        upToken.setPassword(password.toCharArray());
        upToken.setRememberMe(remberMe);
        upToken.setHost(host);
        subject.login(upToken);
    }

    public void login(String ssoid, String host, boolean remberMe) {
        UsernamePasswordToken upToken = new UsernamePasswordToken();
        upToken.setUsername(ssoid);
        // 不能设置为null，设置一个值就可以了，只要和AuthenticationInfo里的一致
        upToken.setPassword(Constants.SHIRO_PASSWORD.toCharArray());
        upToken.setRememberMe(remberMe);
        upToken.setHost(host);
        subject.login(upToken);
    }

    public void logout() {
        subject.logout();
    }

    public void clear() {
        Collection<Object> keys = session.getAttributeKeys();
        for (Object key : keys) {
            session.removeAttribute(key);
        }
    }

    public boolean containsKey(Object key) {
        return keySet().contains(key);
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public Set<Entry<String, Object>> entrySet() {
        Collection<Object> keys = session.getAttributeKeys();
        Map<String, Object> map = new HashMap<String, Object>();
        if (keys != null && keys.size() > 0) {
            for (Object key : keys) {
                map.put((String) key, session.getAttribute(key));
            }
        }
        return map.entrySet();
    }

    public Object get(Object key) {
        return session.getAttribute((String) key);
    }

    public boolean isEmpty() {
        Collection<Object> keys = session.getAttributeKeys();
        return keys == null || keys.size() == 0;
    }

    public Set<String> keySet() {
        Collection<Object> keys = session.getAttributeKeys();
        Map<String, Object> map = new HashMap<String, Object>();
        if (keys != null && keys.size() > 0) {
            for (Object key : keys) {
                map.put((String) key, session.getAttribute(key));
            }
        }
        return map.keySet();
    }

    public Object put(String key, Object value) {
        session.setAttribute(key, value);
        return value;
    }

    public void putAll(Map<? extends String, ? extends Object> t) {
        for (Map.Entry<? extends String, ? extends Object> entry : t.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key) {
        return session.removeAttribute((String) key);
    }

    public int size() {
        Collection<Object> keys = session.getAttributeKeys();
        return (keys == null) ? 0 : keys.size();
    }

    public Collection<Object> values() {
        return session.getAttributeKeys();
    }

    /**
     * 检测权限
     *
     * @param companyId
     * @return
     */
    public void checkPermission(Long companyId) throws ServiceException {

        if (null == getUserInfo()) {
            LOGGER.error("checkPermission error ,userInfo is null ");
            //TODO custom error code
            throw new ServiceException("work.user.sessionExpired");
        }

        Object object = session.getAttribute(Constants.USER_CHANGE_PASSWORD + getUserInfo().getId());

        if (null != object && !object.equals(getUserInfo().getPassword())) {
            LOGGER.error("user changePasswrod ,need login agin");
            throw new ServiceException("work.user.changePassWord");
        }

        Object loginTime = session.getAttribute(Constants.KICK_APP_SESSION_KEY);
        if (null != loginTime){
            LOGGER.error("user already login ,need login out");
            throw new ServiceException("work.user.alreadyLogin", loginTime);
        }

    }

}
