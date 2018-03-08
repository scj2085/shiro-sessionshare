package com.gome.meidian.account.shiroimagecode1;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.octo.captcha.service.image.AbstractManageableImageCaptchaService;

public class SenderUtils {

	private static Logger log = LoggerFactory.getLogger(SenderUtils.class);
	
	/**
	 * 
	 * @description: 发送短信验证码
	 * @param msgUrl
	 * @return
	 * @throws IOException
	 * @author: yaoliang
	 * @time: 2016年7月23日 上午11:26:18
	 */
	public static String send(String msgUrl) throws IOException {

		// String temp = new String(sb.toString().getBytes("GBK"),"UTF-8");
		System.out.println("msgUrl:" + msgUrl);
		log.info("msgUrl:" + msgUrl);
		URL url = new URL(msgUrl);

		// 打开url连接
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// 设置url请求方式 ‘get’ 或者 ‘post’
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(10000);

		// 发送
		InputStream is = url.openStream();

		// 转换返回值
		String returnStr = SenderUtils.convertStreamToString(is);

		// 返回结果为‘0，20140009090990,1，提交成功’ 发送成功 具体见说明文档
		log.info(returnStr);
		// 返回发送结果
		return returnStr;
	}

	/**
	 * 
	 * @description: 批量发送短信验证码：每个手机对应不同的验证码
	 * @param url 服务器地址
	 * @param param 用户名密码及发送内容等
	 * @throws Exception
	 * @author: yaoliang
	 * @time: 2016年7月23日 上午11:23:57
	 */
	public static String send(String url, String param) throws Exception {

		URL localURL = new URL(url);
		URLConnection connection = localURL.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
		httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpURLConnection.setRequestProperty("Content-Length", String.valueOf(param.length()));
		httpURLConnection.setConnectTimeout(10000);

		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		String resultBuffer = "";

		try {
			outputStream = httpURLConnection.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);

			outputStreamWriter.write(param.toString());
			outputStreamWriter.flush();

			if (httpURLConnection.getResponseCode() >= 300) {
				throw new Exception(
						"HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
			}

			inputStream = httpURLConnection.getInputStream();
			resultBuffer = convertStreamToString(inputStream);
			System.out.println(resultBuffer);

		}catch(Exception e){
			e.printStackTrace();
			log.error("SenderUtils.send message exception:"+e.getMessage());
		}finally {

			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}

			if (outputStream != null) {
				outputStream.close();
			}

			if (reader != null) {
				reader.close();
			}

			if (inputStreamReader != null) {
				inputStreamReader.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}

		}

		return resultBuffer;
	}

	/**
	 * 转换返回值类型为UTF-8格式.
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		StringBuilder sb1 = new StringBuilder();
		byte[] bytes = new byte[4096];
		int size = 0;

		try {
			while ((size = is.read(bytes)) > 0) {
				String str = new String(bytes, 0, size, "UTF-8");
				sb1.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb1.toString();
	}

	/**
	 * @description: 发送图形验证码
	 * @param captchaService
	 * @param sessionId
	 * @author: yaoliang
	 * @time: 2016年7月23日 上午11:19:16
	 */
	public static void sendPictureCaptcha(AbstractManageableImageCaptchaService captchaService, String sessionId,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] captchaChallengeAsJpeg = null;
		// 输出jpg的字节流
		ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

		BufferedImage challenge = (BufferedImage) captchaService.getChallengeForID(sessionId, request.getLocale());
		ImageIO.write(challenge, "jpeg", jpegOutputStream);
		captchaChallengeAsJpeg = jpegOutputStream.toByteArray();

		// flush it in the response
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		ServletOutputStream responseOutputStream = response.getOutputStream();
		responseOutputStream.write(captchaChallengeAsJpeg);
		responseOutputStream.flush();
		responseOutputStream.close();
	}

	/**
	 * @description: 验证验证码
	 * @param captchaService
	 * @param sessionId
	 * @author: yaoliang
	 * @time: 2016年7月23日 上午11:19:16
	 */
	public static boolean validateResponseForID(AbstractManageableImageCaptchaService captchaService, String sessionId,
			String pictureCaptcha) {
		return captchaService.validateResponseForID(sessionId, pictureCaptcha);
	}

}
