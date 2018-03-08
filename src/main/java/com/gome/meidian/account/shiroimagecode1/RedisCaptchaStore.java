package com.gome.meidian.account.shiroimagecode1;

import com.gome.meidian.account.shiro.Constants;
import com.gome.meidian.account.shiro.SerializationUtils;
import com.octo.captcha.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaAndLocale;
import com.octo.captcha.service.captchastore.CaptchaStore;

import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 存放验证码类的实现类
 */
public class RedisCaptchaStore implements CaptchaStore {

    @Resource
    private JedisCluster jedisCluster;

    @Override
    public void cleanAndShutdown() {
//				store.clear();
    }

    @Override
    public void empty() {
//		        this.store = new HashMap();
    }
    @Override
    public Captcha getCaptcha(String id) throws CaptchaServiceException {
        byte[] captchaAndLocale = JedisUtils.process(jedisCluster, jedis -> {
            if (jedis.exists(id.getBytes())) {
                return jedis.get(id.getBytes());
            }
            return null;
        });
        return captchaAndLocale!=null?((CaptchaAndLocale) SerializationUtils.deserialize(captchaAndLocale)).getCaptcha():null;
    }

    @Override
    public Collection getKeys() {
//		        return store.keySet();
        return Collections.emptySet();
    }

    @Override
    public Locale getLocale(String id) throws CaptchaServiceException {
//		        Object captchaAndLocale = store.get(id);

        Object captchaAndLocale = JedisUtils.process(jedisCluster, jedis -> {
            if (jedis.exists(id.getBytes())) {
                return jedis.get(id.getBytes());
            }
            return null;
        });

        return captchaAndLocale!=null?((CaptchaAndLocale) captchaAndLocale).getLocale():null;
    }

    @Override
    public int getSize() {
//		        return store.size();
        return 0;
    }

    @Override
    public boolean hasCaptcha(String id) {
        return JedisUtils.process(jedisCluster, jedis -> jedis.exists(id.getBytes()));
    }

    @Override
    public void initAndStart() {
        // Nothing to do with map implementations
    }

    @Override
    public boolean removeCaptcha(String id) {
//				if (store.get(id) != null) {
//		            store.remove(id);
//		            return true;
//		        }
//		        return false;
        return JedisUtils.process(jedisCluster, jedis -> {
            if (jedis.exists(id.getBytes())) {
                jedis.del(id.getBytes());
                return true;
            }
            return false;
        });
    }

    @Override
    public void storeCaptcha(String id, Captcha captcha) throws CaptchaServiceException {
//		        if (store.get(id) != null) {
//	            throw new CaptchaServiceException("a captcha with this id already exist. This error must " +
//	                    "not occurs, this is an implementation pb!");
//				store.put(id, new CaptchaAndLocale(captcha));
        JedisUtils.process(jedisCluster,jedis -> jedis.set(id.getBytes(), SerializationUtils.serialize(new CaptchaAndLocale(captcha)), "NX".getBytes(),
              "EX".getBytes(), Constants.PICTURE_CAPTCHA_EXPIRE_TIME));

    }

    @Override
    public void storeCaptcha(String id, Captcha captcha, Locale locale) throws CaptchaServiceException {
//				store.put(id, new CaptchaAndLocale(captcha,locale));
        JedisUtils.process(jedisCluster, jedis ->
                jedis.set(id.getBytes(), SerializationUtils.serialize(new CaptchaAndLocale(captcha, locale)), "NX".getBytes(),
                        "EX".getBytes(), Constants.PICTURE_CAPTCHA_EXPIRE_TIME)
        );
    }
    
    public static void main(String[] args) {
    	Date date = new Date(TimeUnit.MINUTES.toSeconds(30));
    	System.err.println(TimeUnit.MINUTES.toSeconds(30));
//        long times = date.getTime();//时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        System.err.println(dateString);
	}
}
