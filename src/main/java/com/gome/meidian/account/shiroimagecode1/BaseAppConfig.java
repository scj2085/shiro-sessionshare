package com.gome.meidian.account.shiroimagecode1;

import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gome.meidian.restfulcommon.filter.XssFilter;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.image.AbstractManageableImageCaptchaService;

/**
 * spring 配置入口
 * 
 * @author qupeng
 */
// @Import({DynamicDataSourceRegister.class}) // 注册动态多数据源
@Configuration
public class BaseAppConfig {
	@Resource
	private XssFilter xssFilter;

	// 可以不配置FilterRegistrationBean，默认拦截/*
	// xss 过滤
	@Bean
	public FilterRegistrationBean xssFilterRegistration() {
		FilterRegistrationBean reg = new FilterRegistrationBean();
		reg.setFilter(xssFilter);
		reg.addUrlPatterns("/*");
		reg.setName("xssFilter");
		reg.setOrder(Integer.MAX_VALUE);
		return reg;
	}

	/**
	 * 自定义ackson ObjectMapper
	 * 
	 * @return
	 */
	@Bean
	public ObjectMapper jacksonObjectMapper() {

		ObjectMapper objectMapper = new ObjectMapper();
		// objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
			@Override
			public void serialize(Object value, JsonGenerator jg, SerializerProvider sp)
					throws IOException, JsonProcessingException {
				// 所有null字段，重写为空字符串
//				jg.writeString("");
				sp.getDefaultNullKeySerializer();
			}
		});
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return objectMapper;
	}

	@Bean
	public MeiproCommandLineRunner meiproCommandLineRunner() {
		return new MeiproCommandLineRunner();
	}

	/**
	 * @author qupeng 多个runner 加载顺序，可以实现Ordered 接口或 or 使用annotation at Order
	 */
	public static class MeiproCommandLineRunner implements CommandLineRunner {
		Logger loger = LoggerFactory.getLogger(this.getClass());

		@Override
		public void run(String... args) throws Exception {
			// TODO Auto-generated method stub
			loger.info("------------MeiproCommandLineRunner--------------------");
		}

	}
	
	@Bean
	public CaptchaStore getCaptchaStore(){
//		return new FastHashMapCaptchaStore();
		return new RedisCaptchaStore();
	}
	
	@Bean
	public AbstractManageableImageCaptchaService captchaService(CaptchaStore captchaStore){
//		GenericManageableCaptchaService service = new GenericManageableCaptchaService(cSRCaptchaEngine(),300,20000);
//		System.out.println("------------
 //验证码 init--------------------");
		return new AbstractManageableImageCaptchaService(captchaStore, cSRCaptchaEngine(), 60, 100000, 75000){};
	}

	public ListImageCaptchaEngine cSRCaptchaEngine() {
		return new CustomListImageCaptchaEngine();
	}
}
